package com.afarias.event_challenge.service.impl;

import static com.afarias.event_challenge.config.EventosTestConfig.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.afarias.event_challenge.config.EventosTestConfig;
import com.afarias.event_challenge.dto.AuditoriaResponseDto;
import com.afarias.event_challenge.dto.EventoRequestDto;
import com.afarias.event_challenge.dto.EventoResponseDto;
import com.afarias.event_challenge.entity.Auditoria;
import com.afarias.event_challenge.entity.Evento;
import com.afarias.event_challenge.exception.EntityNotFoundException;
import com.afarias.event_challenge.exception.EventoDuplicadoException;
import com.afarias.event_challenge.mapper.AuditoriaMapper;
import com.afarias.event_challenge.mapper.AuditoriaMapperImpl;
import com.afarias.event_challenge.mapper.EventoMapper;
import com.afarias.event_challenge.mapper.EventoMapperImpl;
import com.afarias.event_challenge.repository.AuditoriaRepository;
import com.afarias.event_challenge.repository.EventoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class EventoServiceImplTest {

    @Mock private EventoRepository eventoRepository;
    @Mock private AuditoriaRepository auditoriaRepository;
    @Spy private EventoMapper eventoMapper = new EventoMapperImpl();
    @Spy private AuditoriaMapper auditoriaMapper = new AuditoriaMapperImpl();
    @InjectMocks private EventoServiceImpl eventoService;

    private final EventosTestConfig datos = new EventosTestConfig();
    private Evento evento;
    private Evento eventoQuality;
    private EventoRequestDto eventoRequest;
    private EventoRequestDto qualityRequest;
    private Auditoria auditoria;

    @BeforeEach
    void setup() {
        evento = datos.expectedEventoFromDB();
        eventoQuality = datos.expectedQualityEventoFromDB();
        eventoRequest = datos.expectedEventoRequest();
        qualityRequest = datos.expectedQualityEventoRequest();
        auditoria = datos.expectedAuditoriaFromDB();
    }

    @Test
    void givenEventosRegistrados_whenListar_thenRetornaEventos() {
        when(eventoRepository.findAll()).thenReturn(List.of(evento));

        List<EventoResponseDto> resultado = eventoService.retornarEventos();

        assertEquals(List.of(datos.expectedEvento()), resultado);
        verify(eventoRepository).findAll();
    }

    @Test
    void givenSinEventos_whenListar_thenRetornaListaVacia() {
        when(eventoRepository.findAll()).thenReturn(List.of());

        assertTrue(eventoService.retornarEventos().isEmpty());
        verify(eventoRepository).findAll();
    }

    @Test
    void givenProduction_whenCrear_thenGuardaEventoSinAuditoria() {
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        EventoResponseDto resultado = eventoService.crearEvento(eventoRequest);

        assertEquals(datos.expectedEvento(), resultado);
        verify(eventoRepository).existsByEventId(EVENT_ID);
        verify(eventoRepository).save(any(Evento.class));
        verifyNoInteractions(auditoriaRepository);
    }

    @Test
    void givenQuality_whenCrear_thenGuardaEventoYAuditoria() {
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoQuality);

        EventoResponseDto resultado = eventoService.crearEvento(qualityRequest);

        ArgumentCaptor<Auditoria> captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditoriaRepository).save(captor.capture());
        assertEquals(datos.expectedQualityEvento(), resultado);
        assertSame(eventoQuality, captor.getValue().getEvent());
        assertNotNull(captor.getValue().getCreatedAt());
        verify(eventoRepository).save(any(Evento.class));
    }

    @Test
    void givenProductionDuplicado_whenCrear_thenRechazaSinGuardar() {
        when(eventoRepository.existsByEventId(EVENT_ID)).thenReturn(true);

        EventoDuplicadoException exception = assertThrows(
                EventoDuplicadoException.class, () -> eventoService.crearEvento(eventoRequest));

        assertEquals(DUPLICADO_MESSAGE, exception.getMessage());
        verify(eventoRepository, never()).save(any(Evento.class));
        verifyNoInteractions(auditoriaRepository);
    }

    @Test
    void givenQualityDuplicado_whenCrear_thenRechazaSinGuardarAuditoria() {
        when(eventoRepository.existsByEventId(EVENT_ID)).thenReturn(true);

        assertThrows(EventoDuplicadoException.class, () -> eventoService.crearEvento(qualityRequest));

        verify(eventoRepository, never()).save(any(Evento.class));
        verifyNoInteractions(auditoriaRepository);
    }

    @Test
    void givenFalloAlGuardarEvento_whenCrearQuality_thenPropagaErrorSinAuditar() {
        DataIntegrityViolationException error = new DataIntegrityViolationException("Insert failed");
        when(eventoRepository.save(any(Evento.class))).thenThrow(error);

        assertSame(error, assertThrows(DataIntegrityViolationException.class,
                () -> eventoService.crearEvento(qualityRequest)));
        verifyNoInteractions(auditoriaRepository);
    }

    @Test
    void givenFalloAlGuardarAuditoria_whenCrearQuality_thenPropagaError() {
        when(eventoRepository.save(any(Evento.class))).thenReturn(eventoQuality);
        DataIntegrityViolationException error = new DataIntegrityViolationException("Audit failed");
        when(auditoriaRepository.save(any(Auditoria.class))).thenThrow(error);

        assertSame(error, assertThrows(DataIntegrityViolationException.class,
                () -> eventoService.crearEvento(qualityRequest)));
        verify(auditoriaRepository).save(any(Auditoria.class));
    }

    @Test
    void givenAuditoriaExistente_whenConsultar_thenRetornaAuditoria() {
        when(auditoriaRepository.findByEventId(EVENTO_ID)).thenReturn(Optional.of(auditoria));

        AuditoriaResponseDto resultado = eventoService.obtenerAuditoriaPorEventoId(EVENTO_ID);

        assertEquals(datos.expectedAuditoria(), resultado);
        verify(auditoriaRepository).findByEventId(EVENTO_ID);
    }

    @Test
    void givenAuditoriaInexistente_whenConsultar_thenLanzaNotFound() {
        when(auditoriaRepository.findByEventId(EVENTO_ID_NO_VALID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> eventoService.obtenerAuditoriaPorEventoId(EVENTO_ID_NO_VALID));

        assertEquals(NOT_FOUND_MESSAGE, exception.getMessage());
        verify(auditoriaRepository).findByEventId(EVENTO_ID_NO_VALID);
    }
}
