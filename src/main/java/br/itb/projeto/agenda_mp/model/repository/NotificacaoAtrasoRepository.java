package br.itb.projeto.agenda_mp.model.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import br.itb.projeto.agenda_mp.model.entity.NotificacaoAtraso;

public interface NotificacaoAtrasoRepository extends JpaRepository<NotificacaoAtraso, Long> {
    boolean existsByAgendaIdAndDataPrevista(Long agendaId, LocalDateTime dataPrevista);
}
