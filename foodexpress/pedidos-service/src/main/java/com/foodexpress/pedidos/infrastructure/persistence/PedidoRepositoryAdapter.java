package com.foodexpress.pedidos.infrastructure.persistence;

import com.foodexpress.pedidos.application.exception.ConflictoConcurrenciaException;
import com.foodexpress.pedidos.application.exception.PedidoNoEncontradoException;
import com.foodexpress.pedidos.application.port.out.PedidoRepository;
import com.foodexpress.pedidos.domain.model.Pedido;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Adaptador de salida: implementa el puerto PedidoRepository usando Spring Data JPA. */
@Repository
public class PedidoRepositoryAdapter implements PedidoRepository {

    private final PedidoJpaRepository jpaRepository;

    public PedidoRepositoryAdapter(PedidoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Pedido guardar(Pedido pedido) {
        if (pedido.getId() == null) {
            PedidoEntity nueva = jpaRepository.saveAndFlush(PedidoMapper.aEntidadNueva(pedido));
            return PedidoMapper.aDominio(nueva);
        }

        PedidoEntity entidad = jpaRepository.findById(pedido.getId())
                .orElseThrow(() -> new PedidoNoEncontradoException(pedido.getId()));
        if (!Objects.equals(entidad.getVersion(), pedido.getVersion())) {
            throw new ConflictoConcurrenciaException(pedido.getId());
        }
        PedidoMapper.copiarEstado(pedido, entidad);
        try {
            return PedidoMapper.aDominio(jpaRepository.saveAndFlush(entidad));
        } catch (OptimisticLockingFailureException e) {
            throw new ConflictoConcurrenciaException(pedido.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(Long pedidoId) {
        return jpaRepository.findById(pedidoId).map(PedidoMapper::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorRestaurante(Long restauranteId) {
        return jpaRepository.findByRestauranteIdOrderByFechaCreacionDesc(restauranteId).stream()
                .map(PedidoMapper::aDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> buscarPorCliente(String clienteEmail) {
        return jpaRepository.findByClienteEmailIgnoreCaseOrderByFechaCreacionDesc(clienteEmail).stream()
                .map(PedidoMapper::aDominio)
                .toList();
    }
}
