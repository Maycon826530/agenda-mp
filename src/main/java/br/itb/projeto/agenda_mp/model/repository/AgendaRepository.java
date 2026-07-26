package br.itb.projeto.agenda_mp.model.repository;

import br.itb.projeto.agenda_mp.model.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByUsuarioId(Long usuarioId);

    @Query("""
            SELECT DISTINCT a
            FROM Agenda a
            WHERE a.horario = :horario
              AND a.dataInicio <= :agora
              AND a.dataFim >= :agora
              AND EXISTS (
                  SELECT m.id
                  FROM Medicamento m
                  WHERE m.agenda = a
                    AND UPPER(m.statusMedicamento) = 'ATIVO'
              )
            """)
    List<Agenda> findAgendasAtivasParaNotificacao(
            @Param("horario") LocalTime horario,
            @Param("agora") LocalDateTime agora
    );
}
