package com.foodexpress.pedidos.infrastructure.web;

import com.foodexpress.pedidos.application.exception.ConflictoConcurrenciaException;
import com.foodexpress.pedidos.application.exception.PedidoNoEncontradoException;
import com.foodexpress.pedidos.application.exception.PlatoNoDisponibleException;
import com.foodexpress.pedidos.application.exception.RestauranteNoDisponibleException;
import com.foodexpress.pedidos.application.exception.ServicioMenuNoDisponibleException;
import com.foodexpress.pedidos.domain.exception.PedidoInvalidoException;
import com.foodexpress.pedidos.domain.exception.TransicionInvalidaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/** Traduce las excepciones de negocio a respuestas HTTP con formato "problem detail". */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ResponseEntity<ProblemDetail> noEncontrado(PedidoNoEncontradoException e) {
        return problema(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(PedidoInvalidoException.class)
    public ResponseEntity<ProblemDetail> pedidoInvalido(PedidoInvalidoException e) {
        return problema(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler({PlatoNoDisponibleException.class, RestauranteNoDisponibleException.class})
    public ResponseEntity<ProblemDetail> noDisponible(RuntimeException e) {
        return problema(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    @ExceptionHandler({TransicionInvalidaException.class, ConflictoConcurrenciaException.class})
    public ResponseEntity<ProblemDetail> conflicto(RuntimeException e) {
        return problema(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(ServicioMenuNoDisponibleException.class)
    public ResponseEntity<ProblemDetail> servicioNoDisponible(ServicioMenuNoDisponibleException e) {
        return problema(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> validacion(MethodArgumentNotValidException e) {
        String detalle = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return problema(HttpStatus.BAD_REQUEST, detalle);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> cuerpoIlegible(HttpMessageNotReadableException e) {
        return problema(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido para esta operación");
    }

    private ResponseEntity<ProblemDetail> problema(HttpStatus estado, String detalle) {
        return ResponseEntity.status(estado).body(ProblemDetail.forStatusAndDetail(estado, detalle));
    }
}
