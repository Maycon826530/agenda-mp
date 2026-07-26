package br.itb.projeto.agenda_mp.model.repository;

import br.itb.projeto.agenda_mp.model.entity.Lembrete;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LembreteRepository extends JpaRepository<Lembrete, Long> {
    List<Lembrete> findByUsuarioIdOrderByDataAscHorarioAsc(Long usuarioId);
    Optional<Lembrete> findByIdAndUsuarioId(Long id, Long usuarioId);

    @EntityGraph(attributePaths = "usuario")
    List<Lembrete> findByDataAndHorario(LocalDate data, LocalTime horario);
    void deleteByUsuarioId(Long usuarioId);
}
