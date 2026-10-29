package com.afarias.event_challenge.exception;

public class EntityNotFoundException extends RuntimeException {

    public <T> EntityNotFoundException(Class<T> entityClass) {
        super("Registro de " + entityClass.getSimpleName() + " no encontrado");
    }
}
