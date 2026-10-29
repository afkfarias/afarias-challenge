package com.afarias.event_challenge.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuditoriaResponseDto {

    Long id;
    Long eventoId;
    String eventId;
    LocalDateTime createdAt;
}
