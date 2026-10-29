package com.afarias.event_challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ErrorResponse {
    String mensaje;

    int codigo;
}
