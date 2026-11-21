package com.foodexpress.pedidos.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoJpaRepository extends JpaRepository<PedidoEntity, Long> {

    List<PedidoEntity> findByRestauranteIdOrderByFechaCreacionDesc(Long restauranteId);

    List<PedidoEntity> findByClienteEmailIgnoreCaseOrderByFechaCreacionDesc(String clienteEmail);
}
