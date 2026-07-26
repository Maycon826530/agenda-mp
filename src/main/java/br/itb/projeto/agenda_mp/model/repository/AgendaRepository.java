package br.itb.projeto.agenda_mp.model.repository;

import br.itb.projeto.agenda_mp.model.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByUsuarioId(Long usuarioId);

    @Query("""
            SELECT DISTINCT a
            FROM Agenda a
            WHERE a.dataInicio <= :agora
              AND a.dataFim >= :inicio
              AND EXISTS (
                  SELECT m.id
                  FROM Medicamento m
                  WHERE m.agenda = a
                    AND UPPER(m.statusMedicamento) = 'ATIVO'
              )
            """)
    List<Agenda> findAgendasAtivasParaNotificacao(
            @Param("inicio") LocalDateTime inicio,
            @Param("agora") LocalDateTime agora
    );
}
