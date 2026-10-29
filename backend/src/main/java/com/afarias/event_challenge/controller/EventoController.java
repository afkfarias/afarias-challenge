package com.afarias.event_challenge.controller;

import com.afarias.event_challenge.dto.AuditoriaResponseDto;
import com.afarias.event_challenge.dto.ErrorResponse;
import com.afarias.event_challenge.dto.EventoRequestDto;
import com.afarias.event_challenge.dto.EventoResponseDto;
import com.afarias.event_challenge.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "Endpoints para gestionar eventos")
public class EventoController {

    private final EventoService eventoService;

    @Operation(
            summary = "Obtener eventos",
            description = "Devuelve una lista de todos los eventos existentes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de eventos recuperado exitosamente.")
    })
    @GetMapping
    public List<EventoResponseDto> obtenerEventos() {
        return eventoService.retornarEventos();
    }

    @Operation(summary = "Crear un nuevo evento", description = "Registra un nuevo evento.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento creado exitosamente."),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida o datos incorrectos.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EventoResponseDto> registrarEvento(@Valid @RequestBody EventoRequestDto eventoRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.crearEvento(eventoRequestDto));
    }

    @Operation(
            summary = "Obtener auditoria por id de evento",
            description = "Retorna un registro de auditoria por identificador del evento asociado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auditoria encontrada."),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro de Auditoria no encontrado.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{eventoId}/auditoria")
    public ResponseEntity<AuditoriaResponseDto> obtenerAuditoriaPorEventoId(@PathVariable Long eventoId) {
        return ResponseEntity.ok(eventoService.obtenerAuditoriaPorEventoId(eventoId));
    }
}
