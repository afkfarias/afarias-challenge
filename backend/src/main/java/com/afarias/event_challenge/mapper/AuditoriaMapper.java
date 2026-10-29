package com.afarias.event_challenge.mapper;

import com.afarias.event_challenge.dto.AuditoriaResponseDto;
import com.afarias.event_challenge.entity.Auditoria;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditoriaMapper {

    @Mapping(target = "eventoId", source = "event.id")
    @Mapping(target = "eventId", source = "event.eventId")
    AuditoriaResponseDto toDto(Auditoria auditoria);
}
