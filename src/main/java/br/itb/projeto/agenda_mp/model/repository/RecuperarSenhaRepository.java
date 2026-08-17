package br.itb.projeto.agenda_mp.model.repository;

import br.itb.projeto.agenda_mp.model.entity.RecuperarSenha;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RecuperarSenhaRepository extends JpaRepository<RecuperarSenha, Integer> {
    Optional<RecuperarSenha> findByEmailAndCodigoAndStatusCodigoTrue(String email, String codigo);
}
