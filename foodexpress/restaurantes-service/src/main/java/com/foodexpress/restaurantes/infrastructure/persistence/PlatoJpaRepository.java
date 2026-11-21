package com.foodexpress.restaurantes.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PlatoJpaRepository extends JpaRepository<PlatoEntity, Long> {

    List<PlatoEntity> findByRestauranteId(Long restauranteId);

    List<PlatoEntity> findByRestauranteIdAndIdIn(Long restauranteId, Collection<Long> ids);
}
