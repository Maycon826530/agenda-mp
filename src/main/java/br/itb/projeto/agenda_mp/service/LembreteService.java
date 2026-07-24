package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Lembrete;
import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.model.repository.LembreteRepository;
import br.itb.projeto.agenda_mp.model.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class LembreteService {

    @Autowired
    private LembreteRepository lembreteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Lembrete> findByUsuarioId(Long usuarioId) {
        return lembreteRepository.findByUsuarioIdOrderByDataAscHorarioAsc(usuarioId);
    }

    public Lembrete save(Lembrete lembrete, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));
        lembrete.setUsuario(usuario);
        return lembreteRepository.save(lembrete);
    }

    @Transactional
    public boolean deleteByIdAndUsuarioId(Long id, Long usuarioId) {
        return lembreteRepository.findByIdAndUsuarioId(id, usuarioId)
                .map(lembrete -> {
                    lembreteRepository.delete(lembrete);
                    return true;
                })
                .orElse(false);
    }
}
