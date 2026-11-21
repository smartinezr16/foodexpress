package com.foodexpress.restaurantes.infrastructure.persistence;

import com.foodexpress.restaurantes.application.port.out.RestauranteRepository;
import com.foodexpress.restaurantes.domain.model.Restaurante;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Adaptador de salida: implementa RestauranteRepository con Spring Data JPA. */
@Repository
public class RestauranteRepositoryAdapter implements RestauranteRepository {

    private final RestauranteJpaRepository jpaRepository;

    public RestauranteRepositoryAdapter(RestauranteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Restaurante guardar(Restaurante restaurante) {
        RestauranteEntity entidad = new RestauranteEntity();
        entidad.setId(restaurante.getId());
        entidad.setNombre(restaurante.getNombre());
        entidad.setDireccion(restaurante.getDireccion());
        entidad.setTelefono(restaurante.getTelefono());
        entidad.setHoraApertura(restaurante.getHoraApertura());
        entidad.setHoraCierre(restaurante.getHoraCierre());
        entidad.setEstado(restaurante.getEstado());
        return aDominio(jpaRepository.save(entidad));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Restaurante> buscarPorId(Long restauranteId) {
        return jpaRepository.findById(restauranteId).map(RestauranteRepositoryAdapter::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existe(Long restauranteId) {
        return jpaRepository.existsById(restauranteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Restaurante> listar() {
        return jpaRepository.findAll(Sort.by("nombre")).stream()
                .map(RestauranteRepositoryAdapter::aDominio)
                .toList();
    }

    private static Restaurante aDominio(RestauranteEntity entidad) {
        return new Restaurante(entidad.getId(), entidad.getNombre(), entidad.getDireccion(), entidad.getTelefono(),
                entidad.getHoraApertura(), entidad.getHoraCierre(), entidad.getEstado());
    }
}
