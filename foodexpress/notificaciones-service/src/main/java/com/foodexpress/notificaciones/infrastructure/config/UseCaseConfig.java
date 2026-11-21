package com.foodexpress.notificaciones.infrastructure.config;

import com.foodexpress.notificaciones.application.port.out.CanalNotificacion;
import com.foodexpress.notificaciones.application.port.out.EventoProcesadoRepository;
import com.foodexpress.notificaciones.application.port.out.NotificacionRepository;
import com.foodexpress.notificaciones.application.usecase.ConsultarNotificacionesUseCase;
import com.foodexpress.notificaciones.application.usecase.DespachadorNotificaciones;
import com.foodexpress.notificaciones.application.usecase.NotificarCambioEstadoUseCase;
import com.foodexpress.notificaciones.application.usecase.NotificarPedidoCanceladoUseCase;
import com.foodexpress.notificaciones.application.usecase.NotificarPedidoCreadoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Punto de ensamblaje. Spring entrega la lista con todos los adaptadores que
 * implementan CanalNotificacion: registrar un canal nuevo no exige tocar esta clase.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public DespachadorNotificaciones despachadorNotificaciones(List<CanalNotificacion> canales,
                                                               NotificacionRepository notificacionRepository) {
        return new DespachadorNotificaciones(canales, notificacionRepository);
    }

    @Bean
    public NotificarPedidoCreadoUseCase notificarPedidoCreadoUseCase(EventoProcesadoRepository eventosProcesados,
                                                                     DespachadorNotificaciones despachador) {
        return new NotificarPedidoCreadoUseCase(eventosProcesados, despachador);
    }

    @Bean
    public NotificarCambioEstadoUseCase notificarCambioEstadoUseCase(EventoProcesadoRepository eventosProcesados,
                                                                     DespachadorNotificaciones despachador) {
        return new NotificarCambioEstadoUseCase(eventosProcesados, despachador);
    }

    @Bean
    public NotificarPedidoCanceladoUseCase notificarPedidoCanceladoUseCase(EventoProcesadoRepository eventosProcesados,
                                                                           DespachadorNotificaciones despachador) {
        return new NotificarPedidoCanceladoUseCase(eventosProcesados, despachador);
    }

    @Bean
    public ConsultarNotificacionesUseCase consultarNotificacionesUseCase(NotificacionRepository notificacionRepository) {
        return new ConsultarNotificacionesUseCase(notificacionRepository);
    }
}
