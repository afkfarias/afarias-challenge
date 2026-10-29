package com.afarias.event_challenge.exception;

public class EventoDuplicadoException extends RuntimeException {
    public EventoDuplicadoException() {
        super("Ya existe un registro para el eventId");
    }
}
