package br.itb.projeto.agenda_mp.model.repository;

import br.itb.projeto.agenda_mp.model.entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AgendaRepository extends JpaRepository<Agenda, Long> {

    List<Agenda> findByUsuarioId(Long usuarioId);

    List<Agenda> findByHorarioAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            LocalTime horario,
            LocalDateTime dataAtualInicio,
            LocalDateTime dataAtualFim
    );
}
