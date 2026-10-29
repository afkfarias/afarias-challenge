package com.afarias.event_challenge.dto;

import com.afarias.event_challenge.enums.EventoTipo;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class EventoResponseDto {

  Long id;

  String eventId;

  LocalDateTime timestamp;

  EventoTipo eventType;

  String description;
}
