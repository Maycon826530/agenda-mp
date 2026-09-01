package br.itb.projeto.agenda_mp.model.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import br.itb.projeto.agenda_mp.model.entity.NotificacaoAtraso;

public interface NotificacaoAtrasoRepository extends JpaRepository<NotificacaoAtraso, Long> {
    boolean existsByAgendaIdAndDataPrevista(Long agendaId, LocalDateTime dataPrevista);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM NotificacaoAtraso n WHERE n.agenda.id IN " +
           "(SELECT a.id FROM Agenda a WHERE a.usuario.id = :usuarioId)")
    int deleteByUsuarioId(Long usuarioId);
}
