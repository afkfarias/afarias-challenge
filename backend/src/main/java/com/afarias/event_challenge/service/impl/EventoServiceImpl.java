package com.afarias.event_challenge.service.impl;

import com.afarias.event_challenge.dto.EventoRequestDto;
import com.afarias.event_challenge.dto.EventoResponseDto;
import com.afarias.event_challenge.exception.EventoDuplicadoException;
import com.afarias.event_challenge.mapper.EventoMapper;
import com.afarias.event_challenge.repository.EventoRepository;
import com.afarias.event_challenge.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

  private final EventoRepository eventoRepository;
  private final EventoMapper eventoMapper;

  @Override
  public List<EventoResponseDto> retornarEventos() {
    return eventoMapper.toDto(eventoRepository.findAll());
  }

  @Override
  public EventoResponseDto crearEvento(EventoRequestDto evento) {
    if (eventoRepository.existsByEventId(evento.getEventId())) {
      throw new EventoDuplicadoException();
    }
    return eventoMapper.toDto(eventoRepository.save(eventoMapper.toEntity(evento)));
  }
}
