package com.afarias.event_challenge.exception;

import com.afarias.event_challenge.dto.ErrorResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException.ConstraintKind;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final String ERROR_INTERNO_SERVIDOR = "Error interno del servidor";
    private final String ERROR_INTERNO_BD = "Error en la base de datos";
    private final String REGISTRO_DUPLICADO = "Ya existe un registro para el eventId";

    private final String SOLICITUD_NO_VALIDA = "La petición no es válida";

    // Manejo de excepciones genérícas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ERROR_INTERNO_SERVIDOR, HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(EventoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleEventoDuplicadoException(EventoDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getMessage(), HttpStatus.CONFLICT.value()));
    }

    // Manejo de excepciones de base de datos general y duplicados
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DataIntegrityViolationException ex) {
        if (isUniqueConstraintViolation(ex)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse(REGISTRO_DUPLICADO, HttpStatus.CONFLICT.value()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ERROR_INTERNO_BD, HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    private boolean isUniqueConstraintViolation(Throwable exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof DuplicateKeyException) {
                return true;
            }
            if (cause instanceof ConstraintViolationException constraintViolation
                    && constraintViolation.getKind() == ConstraintKind.UNIQUE) {
                return true;
            }
        }
        return false;
    }

    // Manejo de excepciones badRequest relacionadas a validaciones en requestDto y request body vacío
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleValidationRequestException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(SOLICITUD_NO_VALIDA, HttpStatus.BAD_REQUEST.value()));
    }

    // Manejo de excepciones para entidades no encontradas
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }
}
