package br.itb.projeto.agenda_mp.model.repository;

import br.itb.projeto.agenda_mp.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}