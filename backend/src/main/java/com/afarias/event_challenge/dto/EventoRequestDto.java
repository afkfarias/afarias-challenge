package com.afarias.event_challenge.dto;

import com.afarias.event_challenge.enums.EventoTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventoRequestDto {
  @NotBlank
  private String eventId;

  @NotNull
  private LocalDateTime timestamp;

  @NotNull
  private EventoTipo eventType;

  @NotBlank
  private String description;
}
