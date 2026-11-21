package com.foodexpress.notificaciones.infrastructure.persistence;

import com.foodexpress.notificaciones.application.port.out.NotificacionRepository;
import com.foodexpress.notificaciones.domain.model.Notificacion;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class NotificacionRepositoryAdapter implements NotificacionRepository {

    private final NotificacionJpaRepository jpaRepository;

    public NotificacionRepositoryAdapter(NotificacionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Notificacion guardar(Notificacion notificacion) {
        return jpaRepository.save(NotificacionEntity.de(notificacion)).aDominio();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notificacion> buscarPorPedido(Long pedidoId) {
        return jpaRepository.findByPedidoIdOrderByFechaAscIdAsc(pedidoId).stream()
                .map(NotificacionEntity::aDominio)
                .toList();
    }
}
