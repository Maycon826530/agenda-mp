package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.model.repository.MedicamentoRepository;
import br.itb.projeto.agenda_mp.model.repository.LembreteRepository;
import br.itb.projeto.agenda_mp.model.repository.UsuarioRepository;
import br.itb.projeto.agenda_mp.rest.dto.AdminMedicamentoDto;
import br.itb.projeto.agenda_mp.rest.dto.AdminUsuarioDto;
import br.itb.projeto.agenda_mp.rest.dto.AdminLembreteDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final UsuarioRepository usuarioRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final LembreteRepository lembreteRepository;
    private final UsuarioService usuarioService;

    public AdminService(UsuarioRepository usuarioRepository,
                        MedicamentoRepository medicamentoRepository, LembreteRepository lembreteRepository,
                        UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.lembreteRepository = lembreteRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<AdminUsuarioDto> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> AdminUsuarioDto.from(usuario, medicamentosDoUsuario(usuario), List.of()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<AdminUsuarioDto> buscarUsuario(Long id) {
        return usuarioRepository.findById(id).map(usuario ->
                AdminUsuarioDto.from(usuario, medicamentosDoUsuario(usuario), lembretesDoUsuario(usuario)));
    }

    public Optional<Usuario> atualizarUsuario(Long id, Usuario usuario) {
        return usuarioRepository.findById(id).flatMap(usuarioAtual -> {
            // Dados de saúde podem ser consultados pelo administrador, mas não alterados.
            usuario.setComorbidade(usuarioAtual.getComorbidade());
            return usuarioService.update(id, usuario);
        });
    }
    public boolean excluirUsuario(Long id) {
        return usuarioService.deleteByAdmin(id);
    }

    private List<AdminMedicamentoDto> medicamentosDoUsuario(Usuario usuario) {
        return medicamentoRepository.findByAgendaUsuarioId(usuario.getId()).stream()
                .map(AdminMedicamentoDto::from)
                .toList();
    }

    private List<AdminLembreteDto> lembretesDoUsuario(Usuario usuario) {
        return lembreteRepository.findByUsuarioIdOrderByDataAscHorarioAsc(usuario.getId()).stream()
                .map(AdminLembreteDto::from)
                .toList();
    }
}
