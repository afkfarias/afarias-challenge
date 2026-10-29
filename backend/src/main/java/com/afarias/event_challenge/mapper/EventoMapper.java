package com.afarias.event_challenge.mapper;

import com.afarias.event_challenge.dto.EventoRequestDto;
import com.afarias.event_challenge.dto.EventoResponseDto;
import com.afarias.event_challenge.entity.Evento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventoMapper {

  EventoResponseDto toDto(Evento eventoEntity);

  @Mapping(target = "id", ignore = true)
  Evento toEntity(EventoRequestDto eventoRequestDto);

  List<EventoResponseDto> toDto(List<Evento> listEventos);
}
