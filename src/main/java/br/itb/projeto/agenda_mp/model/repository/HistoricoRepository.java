package br.itb.projeto.agenda_mp.model.repository;

import br.itb.projeto.agenda_mp.model.entity.Historico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface HistoricoRepository extends JpaRepository<Historico, Long> {
    List<Historico> findByAgendaId(Long agendaId);
    List<Historico> findByAgendaUsuarioId(Long usuarioId);
    List<Historico> findByStatus(String status);

    @Query("""
            SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END
            FROM Historico h
            WHERE h.agenda.id = :agendaId
              AND ((h.status = 'TOMADO' AND h.dataConfirmacao >= :inicio AND h.dataConfirmacao < :fim)
                OR (h.status = 'IGNORADO' AND h.dataHoraIgnorado >= :inicio AND h.dataHoraIgnorado < :fim))
            """)
    boolean existeResolucaoNoPeriodo(Long agendaId, LocalDateTime inicio, LocalDateTime fim);

    @Modifying
    @Query("DELETE FROM Historico h WHERE h.agenda.id = :agendaId")
    void deleteByAgendaId(Long agendaId);

    @Modifying
    @Query("DELETE FROM Historico h WHERE h.medicamento.id = :medicamentoId")
    void deleteByMedicamentoId(Long medicamentoId);
}
