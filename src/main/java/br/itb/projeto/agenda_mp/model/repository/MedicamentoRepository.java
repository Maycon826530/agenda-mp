package br.itb.projeto.agenda_mp.model.repository;

import br.itb.projeto.agenda_mp.model.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    List<Medicamento> findByAgendaId(Long agendaId);
    List<Medicamento> findByAgendaUsuarioId(Long usuarioId);
    List<Medicamento> findByStatusMedicamento(String status);

    @Modifying
    @Query("DELETE FROM Medicamento m WHERE m.agenda.id = :agendaId")
    void deleteByAgendaId(Long agendaId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM Medicamento m WHERE m.agenda.id IN " +
           "(SELECT a.id FROM Agenda a WHERE a.usuario.id = :usuarioId)")
    int deleteByUsuarioId(Long usuarioId);
}
