package com.afarias.event_challenge.controller;

import static com.afarias.event_challenge.config.EventosTestConfig.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.afarias.event_challenge.config.EventosTestConfig;
import com.afarias.event_challenge.dto.AuditoriaResponseDto;
import com.afarias.event_challenge.dto.EventoRequestDto;
import com.afarias.event_challenge.dto.EventoResponseDto;
import com.afarias.event_challenge.entity.Auditoria;
import com.afarias.event_challenge.enums.EventoTipo;
import com.afarias.event_challenge.exception.EntityNotFoundException;
import com.afarias.event_challenge.exception.EventoDuplicadoException;
import com.afarias.event_challenge.service.EventoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException.ConstraintKind;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EventoController.class)
@Import(EventosTestConfig.class)
class EventoControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private EventoService eventoService;
    @Autowired private EventoRequestDto expectedEventoRequest;
    @Autowired private EventoRequestDto expectedQualityEventoRequest;
    @Autowired private EventoResponseDto expectedEvento;
    @Autowired private EventoResponseDto expectedQualityEvento;
    @Autowired private AuditoriaResponseDto expectedAuditoria;

    @Test
    void givenEventosRegistrados_whenListar_thenRetorna200() throws Exception {
        when(eventoService.retornarEventos()).thenReturn(List.of(expectedEvento));

        mockMvc.perform(get(EVENTOS_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(EVENTO_ID.intValue()))
                .andExpect(jsonPath("$[0].eventId").value(EVENT_ID))
                .andExpect(jsonPath("$[0].timestamp").value(TIMESTAMP_JSON));

        verify(eventoService).retornarEventos();
    }

    @Test
    void givenSinEventos_whenListar_thenRetornaListaVacia() throws Exception {
        when(eventoService.retornarEventos()).thenReturn(List.of());

        mockMvc.perform(get(EVENTOS_PATH))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void givenProductionValido_whenCrear_thenRetorna201() throws Exception {
        when(eventoService.crearEvento(expectedEventoRequest)).thenReturn(expectedEvento);

        mockMvc.perform(post(EVENTOS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedEventoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value(EVENT_ID))
                .andExpect(jsonPath("$.eventType").value(EventoTipo.PRODUCTION.name()));

        verify(eventoService).crearEvento(expectedEventoRequest);
    }

    @Test
    void givenQualityValido_whenCrear_thenRetorna201() throws Exception {
        when(eventoService.crearEvento(expectedQualityEventoRequest)).thenReturn(expectedQualityEvento);

        mockMvc.perform(post(EVENTOS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedQualityEventoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value(EVENT_ID))
                .andExpect(jsonPath("$.eventType").value(EventoTipo.QUALITY.name()));

        verify(eventoService).crearEvento(expectedQualityEventoRequest);
    }

    @Test
    void givenEventoDuplicado_whenCrear_thenRetorna409() throws Exception {
        when(eventoService.crearEvento(expectedEventoRequest))
                .thenThrow(new EventoDuplicadoException());

        mockMvc.perform(post(EVENTOS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedEventoRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value(409))
                .andExpect(jsonPath("$.mensaje").value(DUPLICADO_MESSAGE));
    }

    @Test
    void givenDuplicadoEnBaseDeDatos_whenCrear_thenRetorna409() throws Exception {
        when(eventoService.crearEvento(expectedEventoRequest))
                .thenThrow(new DuplicateKeyException("Duplicate key"));

        mockMvc.perform(post(EVENTOS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedEventoRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value(DUPLICADO_MESSAGE));
    }

    @Test
    void givenUniqueDeHibernate_whenCrear_thenRetorna409() throws Exception {
        ConstraintViolationException constraint = new ConstraintViolationException(
                "Unique constraint", new SQLException(), ConstraintKind.UNIQUE, null);
        when(eventoService.crearEvento(expectedEventoRequest))
                .thenThrow(new DataIntegrityViolationException("Insert failed", constraint));

        mockMvc.perform(post(EVENTOS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedEventoRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value(DUPLICADO_MESSAGE));
    }

    @Test
    void givenCamposObligatoriosAusentes_whenCrear_thenRetorna400() throws Exception {
        mockMvc.perform(post(EVENTOS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EventoRequestDto())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(INVALID_REQUEST_MESSAGE));

        verifyNoInteractions(eventoService);
    }

    @Test
    void givenEventIdEnBlanco_whenCrear_thenRetorna400() throws Exception {
        EventoRequestDto request = new EventoRequestDto("   ", TIMESTAMP,
                EventoTipo.PRODUCTION, DESCRIPTION);

        mockMvc.perform(post(EVENTOS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(INVALID_REQUEST_MESSAGE));

        verifyNoInteractions(eventoService);
    }

    @Test
    void givenDescripcionEnBlanco_whenCrear_thenRetorna400() throws Exception {
        EventoRequestDto request = new EventoRequestDto(EVENT_ID, TIMESTAMP,
                EventoTipo.PRODUCTION, "   ");

        mockMvc.perform(post(EVENTOS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(INVALID_REQUEST_MESSAGE));

        verifyNoInteractions(eventoService);
    }

    @Test
    void givenCuerpoVacio_whenCrear_thenRetorna400() throws Exception {
        mockMvc.perform(post(EVENTOS_PATH).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(INVALID_REQUEST_MESSAGE));

        verifyNoInteractions(eventoService);
    }

    @Test
    void givenJsonInvalido_whenCrear_thenRetorna400() throws Exception {
        mockMvc.perform(post(EVENTOS_PATH).contentType(MediaType.APPLICATION_JSON).content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(INVALID_REQUEST_MESSAGE));

        verifyNoInteractions(eventoService);
    }

    @Test
    void givenTipoDesconocido_whenCrear_thenRetorna400() throws Exception {
        String payload = objectMapper.writeValueAsString(expectedEventoRequest)
                .replace("PRODUCTION", "UNKNOWN");

        mockMvc.perform(post(EVENTOS_PATH).contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(INVALID_REQUEST_MESSAGE));

        verifyNoInteractions(eventoService);
    }

    @Test
    void givenTimestampInvalido_whenCrear_thenRetorna400() throws Exception {
        String payload = objectMapper.writeValueAsString(expectedEventoRequest)
                .replace(TIMESTAMP_JSON, "not-a-date");

        mockMvc.perform(post(EVENTOS_PATH).contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(INVALID_REQUEST_MESSAGE));

        verifyNoInteractions(eventoService);
    }

    @Test
    void givenErrorDeIntegridad_whenCrear_thenRetorna500() throws Exception {
        when(eventoService.crearEvento(expectedEventoRequest))
                .thenThrow(new DataIntegrityViolationException("Internal database details"));

        mockMvc.perform(post(EVENTOS_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expectedEventoRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value(DATABASE_ERROR_MESSAGE));
    }

    @Test
    void givenErrorInesperado_whenListar_thenRetorna500() throws Exception {
        when(eventoService.retornarEventos()).thenThrow(new IllegalStateException("Internal details"));

        mockMvc.perform(get(EVENTOS_PATH))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value(INTERNAL_ERROR_MESSAGE));
    }

    @Test
    void givenAuditoriaExistente_whenConsultar_thenRetorna200() throws Exception {
        when(eventoService.obtenerAuditoriaPorEventoId(EVENTO_ID)).thenReturn(expectedAuditoria);

        mockMvc.perform(get(AUDITORIA_PATH, EVENTO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(AUDITORIA_ID.intValue()))
                .andExpect(jsonPath("$.eventoId").value(EVENTO_ID.intValue()))
                .andExpect(jsonPath("$.eventId").value(EVENT_ID))
                .andExpect(jsonPath("$.createdAt").value(CREATED_AT_JSON));

        verify(eventoService).obtenerAuditoriaPorEventoId(EVENTO_ID);
    }

    @Test
    void givenAuditoriaInexistente_whenConsultar_thenRetorna404() throws Exception {
        when(eventoService.obtenerAuditoriaPorEventoId(EVENTO_ID_NO_VALID))
                .thenThrow(new EntityNotFoundException(Auditoria.class));

        mockMvc.perform(get(AUDITORIA_PATH, EVENTO_ID_NO_VALID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(NOT_FOUND_MESSAGE));

        verify(eventoService).obtenerAuditoriaPorEventoId(EVENTO_ID_NO_VALID);
    }

    @Test
    void givenEventoIdInvalido_whenConsultarAuditoria_thenRetorna400() throws Exception {
        mockMvc.perform(get(AUDITORIA_PATH, "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(INVALID_REQUEST_MESSAGE));

        verifyNoInteractions(eventoService);
    }
}
