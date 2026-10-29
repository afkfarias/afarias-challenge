package com.afarias.event_challenge.service.impl;

import com.afarias.event_challenge.dto.AuditoriaResponseDto;
import com.afarias.event_challenge.dto.EventoRequestDto;
import com.afarias.event_challenge.dto.EventoResponseDto;
import com.afarias.event_challenge.entity.Auditoria;
import com.afarias.event_challenge.entity.Evento;
import com.afarias.event_challenge.enums.EventoTipo;
import com.afarias.event_challenge.exception.EntityNotFoundException;
import com.afarias.event_challenge.exception.EventoDuplicadoException;
import com.afarias.event_challenge.mapper.AuditoriaMapper;
import com.afarias.event_challenge.mapper.EventoMapper;
import com.afarias.event_challenge.repository.AuditoriaRepository;
import com.afarias.event_challenge.repository.EventoRepository;
import com.afarias.event_challenge.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

  private final EventoRepository eventoRepository;
  private final AuditoriaRepository auditoriaRepository;
  private final EventoMapper eventoMapper;
  private final AuditoriaMapper auditoriaMapper;

  @Override
  public List<EventoResponseDto> retornarEventos() {
    return eventoMapper.toDto(eventoRepository.findAll());
  }

  @Override
  @Transactional
  public EventoResponseDto crearEvento(EventoRequestDto evento) {
    if (eventoRepository.existsByEventId(evento.getEventId())) {
      throw new EventoDuplicadoException();
    }

    Evento eventSaved = eventoRepository.save(eventoMapper.toEntity(evento));

    if(EventoTipo.QUALITY.equals(eventSaved.getEventType())){
        auditoriaRepository.save(Auditoria.builder()
                .event(eventSaved)
                .createdAt(LocalDateTime.now())
                .build());
    }
    return eventoMapper.toDto(eventSaved);
  }

  @Override
  @Transactional(readOnly = true)
  public AuditoriaResponseDto obtenerAuditoriaPorEventoId(Long eventoId) {
    Auditoria auditoria = auditoriaRepository.findByEventId(eventoId)
        .orElseThrow(() -> new EntityNotFoundException(Auditoria.class));
    return auditoriaMapper.toDto(auditoria);
  }
}
