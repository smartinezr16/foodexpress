package com.foodexpress.restaurantes.infrastructure.config;

import com.foodexpress.restaurantes.application.dto.DatosPlato;
import com.foodexpress.restaurantes.application.dto.DatosRestaurante;
import com.foodexpress.restaurantes.application.port.in.MenuAdminService;
import com.foodexpress.restaurantes.application.port.in.RestauranteService;
import com.foodexpress.restaurantes.domain.model.CategoriaPlato;
import com.foodexpress.restaurantes.domain.model.EstadoRestaurante;
import com.foodexpress.restaurantes.domain.model.Plato;
import com.foodexpress.restaurantes.domain.model.Restaurante;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Carga dos restaurantes de ejemplo la primera vez que arranca el servicio, para
 * poder probar el sistema de inmediato. Se desactiva con foodexpress.seed.enabled=false.
 */
@Component
@ConditionalOnProperty(name = "foodexpress.seed.enabled", havingValue = "true")
public class DatosDemoSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosDemoSeeder.class);

    private final RestauranteService restaurantes;
    private final MenuAdminService menu;

    public DatosDemoSeeder(RestauranteService restaurantes, MenuAdminService menu) {
        this.restaurantes = restaurantes;
        this.menu = menu;
    }

    @Override
    public void run(String... args) {
        if (!restaurantes.listar().isEmpty()) {
            return;
        }

        Restaurante brasa = restaurantes.registrar(new DatosRestaurante(
                "La Brasa Roja", "Calle 45 # 13-20, Bogotá", "6015551234", LocalTime.of(11, 0), LocalTime.of(22, 0)));
        plato(brasa, "Empanadas de carne (x3)", "Con ají de la casa", "9000", CategoriaPlato.ENTRADA, null);
        Plato hamburguesa = plato(brasa, "Hamburguesa clásica", "Carne de res 150 g, lechuga y tomate", "25000",
                CategoriaPlato.PLATO_FUERTE, null);
        menu.agregarExtra(hamburguesa.getId(), "Queso cheddar", new BigDecimal("3000"));
        menu.agregarExtra(hamburguesa.getId(), "Tocineta", new BigDecimal("4000"));
        Plato costillas = plato(brasa, "Costillas BBQ", "Porción de 400 g con papas", "42000",
                CategoriaPlato.PLATO_FUERTE, 5);
        menu.agregarExtra(costillas.getId(), "Papas adicionales", new BigDecimal("6000"));
        plato(brasa, "Limonada natural", "Vaso de 12 onzas", "6000", CategoriaPlato.BEBIDA, null);
        plato(brasa, "Brownie con helado", "Brownie tibio con helado de vainilla", "12000", CategoriaPlato.POSTRE, 8);
        restaurantes.cambiarEstado(brasa.getId(), EstadoRestaurante.ABIERTO);

        Restaurante sushi = restaurantes.registrar(new DatosRestaurante(
                "Sushi Kai", "Carrera 11 # 93-45, Bogotá", "6015559876", LocalTime.of(12, 0), LocalTime.of(23, 0)));
        plato(sushi, "Edamames", "Con sal marina", "11000", CategoriaPlato.ENTRADA, null);
        Plato rollo = plato(sushi, "Rollo California", "10 bocados con cangrejo y aguacate", "28000",
                CategoriaPlato.PLATO_FUERTE, null);
        menu.agregarExtra(rollo.getId(), "Salsa de anguila", new BigDecimal("2500"));
        plato(sushi, "Té verde frío", "Botella de 400 ml", "7000", CategoriaPlato.BEBIDA, null);
        restaurantes.cambiarEstado(sushi.getId(), EstadoRestaurante.ABIERTO);

        log.info("Datos de demostración cargados: restaurantes {} y {}", brasa.getId(), sushi.getId());
    }

    private Plato plato(Restaurante restaurante, String nombre, String descripcion, String precio,
                        CategoriaPlato categoria, Integer unidades) {
        return menu.agregarPlato(restaurante.getId(),
                new DatosPlato(nombre, descripcion, new BigDecimal(precio), categoria, unidades));
    }
}
