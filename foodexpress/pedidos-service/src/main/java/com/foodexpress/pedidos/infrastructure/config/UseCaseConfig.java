package com.foodexpress.pedidos.infrastructure.config;

import com.foodexpress.pedidos.application.dto.ParametrosPedido;
import com.foodexpress.pedidos.application.port.out.MenuRepository;
import com.foodexpress.pedidos.application.port.out.PedidoEventPublisher;
import com.foodexpress.pedidos.application.port.out.PedidoRepository;
import com.foodexpress.pedidos.application.usecase.AvanzarEstadoPedidoUseCase;
import com.foodexpress.pedidos.application.usecase.CancelarPedidoUseCase;
import com.foodexpress.pedidos.application.usecase.ConsultarPedidosUseCase;
import com.foodexpress.pedidos.application.usecase.CrearPedidoUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * Punto de ensamblaje: aquí se conectan los casos de uso con sus adaptadores.
 * Los casos de uso son clases Java sin anotaciones de Spring; el framework solo
 * aparece en esta capa.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public ParametrosPedido parametrosPedido(
            @Value("${foodexpress.pedidos.tasa-impuesto}") BigDecimal tasaImpuesto,
            @Value("${foodexpress.pedidos.costo-domicilio}") BigDecimal costoDomicilio) {
        return new ParametrosPedido(tasaImpuesto, costoDomicilio);
    }

    @Bean
    public CrearPedidoUseCase crearPedidoUseCase(MenuRepository menuRepository,
                                                 PedidoRepository pedidoRepository,
                                                 PedidoEventPublisher eventPublisher,
                                                 ParametrosPedido parametros) {
        return new CrearPedidoUseCase(menuRepository, pedidoRepository, eventPublisher, parametros);
    }

    @Bean
    public AvanzarEstadoPedidoUseCase avanzarEstadoPedidoUseCase(PedidoRepository pedidoRepository,
                                                                 PedidoEventPublisher eventPublisher) {
        return new AvanzarEstadoPedidoUseCase(pedidoRepository, eventPublisher);
    }

    @Bean
    public CancelarPedidoUseCase cancelarPedidoUseCase(PedidoRepository pedidoRepository,
                                                       PedidoEventPublisher eventPublisher) {
        return new CancelarPedidoUseCase(pedidoRepository, eventPublisher);
    }

    @Bean
    public ConsultarPedidosUseCase consultarPedidosUseCase(PedidoRepository pedidoRepository) {
        return new ConsultarPedidosUseCase(pedidoRepository);
    }
}
