package com.afarias.event_challenge.repository;

import com.afarias.event_challenge.entity.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

   /**
    * Valida si existe un evento para un eventId
    * @param eventId
    * @return True o False
   */
  boolean existsByEventId(String eventId);
}
