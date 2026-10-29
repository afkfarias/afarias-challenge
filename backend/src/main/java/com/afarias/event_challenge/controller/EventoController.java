package com.afarias.event_challenge.controller;

import com.afarias.event_challenge.dto.AuditoriaResponseDto;
import com.afarias.event_challenge.dto.EventoRequestDto;
import com.afarias.event_challenge.dto.EventoResponseDto;
import com.afarias.event_challenge.service.EventoService;
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
public class EventoController {

    private final EventoService eventoService;

    @GetMapping
    public List<EventoResponseDto> obtenerEventos() {
        return eventoService.retornarEventos();
    }

    @PostMapping
    public ResponseEntity<EventoResponseDto> registrarEvento(@Valid @RequestBody EventoRequestDto eventoRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.crearEvento(eventoRequestDto));
    }

    @GetMapping("/{eventoId}/auditoria")
    public ResponseEntity<AuditoriaResponseDto> obtenerAuditoriaPorEventoId(@PathVariable Long eventoId) {
        return ResponseEntity.ok(eventoService.obtenerAuditoriaPorEventoId(eventoId));
    }
}
