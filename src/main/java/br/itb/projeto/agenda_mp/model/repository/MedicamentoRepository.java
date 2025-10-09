package br.itb.projeto.agenda_mp.model.repository;

import br.itb.projeto.agenda_mp.model.entity.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
}