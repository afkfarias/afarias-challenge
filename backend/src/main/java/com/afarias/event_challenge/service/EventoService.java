package com.afarias.event_challenge.service;

import com.afarias.event_challenge.dto.AuditoriaResponseDto;
import com.afarias.event_challenge.dto.EventoRequestDto;
import com.afarias.event_challenge.dto.EventoResponseDto;

import java.util.List;

public interface EventoService {

  /**
   * Obtiene una lista de los eventos existentes.
   *
   * @return Lista de EventoResponseDto con la información de los eventos.
   */
  List<EventoResponseDto> retornarEventos();

  /**
   * Crea un nuevo evento en la base de datos.
   *
   * @param evento Datos del evento a crear.
   * @return EventoResponseDto con la información del evento creado.
   */
  EventoResponseDto crearEvento(EventoRequestDto evento);

  AuditoriaResponseDto obtenerAuditoriaPorEventoId(Long eventoId);
}
