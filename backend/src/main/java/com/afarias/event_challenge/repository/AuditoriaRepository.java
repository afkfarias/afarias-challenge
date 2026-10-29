package com.afarias.event_challenge.repository;

import com.afarias.event_challenge.entity.Auditoria;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {
    Optional<Auditoria> findByEventId(Long eventoId);
}
