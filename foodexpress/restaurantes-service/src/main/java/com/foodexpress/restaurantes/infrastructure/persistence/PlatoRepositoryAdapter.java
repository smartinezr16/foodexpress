package com.foodexpress.restaurantes.infrastructure.persistence;

import com.foodexpress.restaurantes.application.exception.ConflictoConcurrenciaException;
import com.foodexpress.restaurantes.application.port.out.PlatoRepository;
import com.foodexpress.restaurantes.domain.exception.RecursoNoEncontradoException;
import com.foodexpress.restaurantes.domain.model.Plato;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Adaptador de salida: implementa PlatoRepository con Spring Data JPA. */
@Repository
public class PlatoRepositoryAdapter implements PlatoRepository {

    private final PlatoJpaRepository jpaRepository;

    public PlatoRepositoryAdapter(PlatoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Plato guardar(Plato plato) {
        PlatoEntity entidad;
        if (plato.getId() == null) {
            entidad = new PlatoEntity();
        } else {
            entidad = jpaRepository.findById(plato.getId())
                    .orElseThrow(() -> RecursoNoEncontradoException.plato(plato.getId()));
            if (!Objects.equals(entidad.getVersion(), plato.getVersion())) {
                throw new ConflictoConcurrenciaException(plato.getId());
            }
        }
        PlatoMapper.copiar(plato, entidad);
        try {
            return PlatoMapper.aDominio(jpaRepository.saveAndFlush(entidad));
        } catch (OptimisticLockingFailureException e) {
            throw new ConflictoConcurrenciaException(plato.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Plato> buscarPorId(Long platoId) {
        return jpaRepository.findById(platoId).map(PlatoMapper::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Plato> buscarPorRestaurante(Long restauranteId) {
        return jpaRepository.findByRestauranteId(restauranteId).stream()
                .map(PlatoMapper::aDominio)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Plato> buscarPorRestauranteYIds(Long restauranteId, Collection<Long> platoIds) {
        return jpaRepository.findByRestauranteIdAndIdIn(restauranteId, platoIds).stream()
                .map(PlatoMapper::aDominio)
                .toList();
    }

    @Override
    @Transactional
    public void eliminar(Long platoId) {
        jpaRepository.deleteById(platoId);
    }
}
