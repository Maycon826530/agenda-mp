package br.itb.projeto.agenda_mp.model.repository;

import br.itb.projeto.agenda_mp.model.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {
}