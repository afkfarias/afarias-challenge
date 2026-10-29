package com.afarias.event_challenge.dto;

import com.afarias.event_challenge.enums.EventoTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "EventoRequestDto", description = "Dto para crear un evento.")
public class EventoRequestDto {
  @Schema(description = "Identificador del evento", example = "EVT-1001")
  @NotBlank
  private String eventId;

  @Schema(description = "Fecha y hora del evento", example = "2026-05-20T10:00:00")
  @NotNull
  private LocalDateTime timestamp;

  @Schema(description = "Tipo de evento", example = "FC Barcelona")
  @NotNull
  private EventoTipo eventType;

  @Schema(description = "Descripción del evento", example = "Production line started")
  @NotBlank
  private String description;
}
