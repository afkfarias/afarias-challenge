package com.afarias.event_challenge.config;

import com.afarias.event_challenge.dto.AuditoriaResponseDto;
import com.afarias.event_challenge.dto.EventoRequestDto;
import com.afarias.event_challenge.dto.EventoResponseDto;
import com.afarias.event_challenge.entity.Auditoria;
import com.afarias.event_challenge.entity.Evento;
import com.afarias.event_challenge.enums.EventoTipo;
import java.time.LocalDateTime;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class EventosTestConfig {

    public static final String EVENTOS_PATH = "/eventos";
    public static final String AUDITORIA_PATH = "/eventos/{eventoId}/auditoria";
    public static final Long EVENTO_ID = 1L;
    public static final Long EVENTO_ID_NO_VALID = 99L;
    public static final Long AUDITORIA_ID = 10L;
    public static final String EVENT_ID = "EVT-1001";
    public static final String DESCRIPTION = "Production line started";
    public static final String TIMESTAMP_JSON = "2026-05-20T10:00:00";
    public static final LocalDateTime TIMESTAMP = LocalDateTime.parse(TIMESTAMP_JSON);
    public static final String CREATED_AT_JSON = "2026-05-20T10:01:00";
    public static final LocalDateTime CREATED_AT = LocalDateTime.parse(CREATED_AT_JSON);
    public static final String DUPLICADO_MESSAGE = "Ya existe un registro para el eventId";
    public static final String INVALID_REQUEST_MESSAGE = "La petición no es válida";
    public static final String NOT_FOUND_MESSAGE = "Registro de Auditoria no encontrado";
    public static final String DATABASE_ERROR_MESSAGE = "Error en la base de datos";
    public static final String INTERNAL_ERROR_MESSAGE = "Error interno del servidor";

    @Bean
    public Evento expectedEventoFromDB() {
        return Evento.builder()
                .id(EVENTO_ID)
                .eventId(EVENT_ID)
                .timestamp(TIMESTAMP)
                .eventType(EventoTipo.PRODUCTION)
                .description(DESCRIPTION)
                .build();
    }

    @Bean
    public Evento expectedQualityEventoFromDB() {
        return Evento.builder()
                .id(EVENTO_ID)
                .eventId(EVENT_ID)
                .timestamp(TIMESTAMP)
                .eventType(EventoTipo.QUALITY)
                .description(DESCRIPTION)
                .build();
    }

    @Bean
    public EventoResponseDto expectedEvento() {
        return EventoResponseDto.builder()
                .id(EVENTO_ID)
                .eventId(EVENT_ID)
                .timestamp(TIMESTAMP)
                .eventType(EventoTipo.PRODUCTION)
                .description(DESCRIPTION)
                .build();
    }

    @Bean
    public EventoResponseDto expectedQualityEvento() {
        return EventoResponseDto.builder()
                .id(EVENTO_ID)
                .eventId(EVENT_ID)
                .timestamp(TIMESTAMP)
                .eventType(EventoTipo.QUALITY)
                .description(DESCRIPTION)
                .build();
    }

    @Bean
    public EventoRequestDto expectedEventoRequest() {
        return new EventoRequestDto(EVENT_ID, TIMESTAMP, EventoTipo.PRODUCTION, DESCRIPTION);
    }

    @Bean
    public EventoRequestDto expectedQualityEventoRequest() {
        return new EventoRequestDto(EVENT_ID, TIMESTAMP, EventoTipo.QUALITY, DESCRIPTION);
    }

    @Bean
    public Auditoria expectedAuditoriaFromDB() {
        return Auditoria.builder()
                .id(AUDITORIA_ID)
                .event(expectedQualityEventoFromDB())
                .createdAt(CREATED_AT)
                .build();
    }

    @Bean
    public AuditoriaResponseDto expectedAuditoria() {
        return AuditoriaResponseDto.builder()
                .id(AUDITORIA_ID)
                .eventoId(EVENTO_ID)
                .eventId(EVENT_ID)
                .createdAt(CREATED_AT)
                .build();
    }
}
