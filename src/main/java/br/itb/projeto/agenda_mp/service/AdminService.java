package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Agenda;
import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.model.repository.AgendaRepository;
import br.itb.projeto.agenda_mp.model.repository.MedicamentoRepository;
import br.itb.projeto.agenda_mp.model.repository.UsuarioRepository;
import br.itb.projeto.agenda_mp.rest.dto.AdminMedicamentoDto;
import br.itb.projeto.agenda_mp.rest.dto.AdminUsuarioDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    private final UsuarioRepository usuarioRepository;
    private final AgendaRepository agendaRepository;
    private final MedicamentoRepository medicamentoRepository;

    public AdminService(UsuarioRepository usuarioRepository, AgendaRepository agendaRepository,
                        MedicamentoRepository medicamentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.agendaRepository = agendaRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUsuarioDto> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(usuario -> AdminUsuarioDto.from(usuario, medicamentosDoUsuario(usuario)))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<AdminUsuarioDto> buscarUsuario(Long id) {
        return usuarioRepository.findById(id).map(usuario ->
                AdminUsuarioDto.from(usuario, medicamentosDoUsuario(usuario)));
    }

    private List<AdminMedicamentoDto> medicamentosDoUsuario(Usuario usuario) {
        return agendaRepository.findByUsuarioId(usuario.getId()).stream()
                .map(Agenda::getId)
                .flatMap(agendaId -> medicamentoRepository.findByAgendaId(agendaId).stream())
                .map(AdminMedicamentoDto::from)
                .toList();
    }
}
