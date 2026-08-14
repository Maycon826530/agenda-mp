package br.itb.projeto.agenda_mp.rest.dto;

import br.itb.projeto.agenda_mp.model.entity.Role;
import br.itb.projeto.agenda_mp.model.entity.Usuario;
import java.time.LocalDate;
import java.util.List;

public record AdminUsuarioDto(Long id, String nome, String email, LocalDate dataNascimento,
                              String comorbidade, Role role, List<AdminMedicamentoDto> medicamentos,
                              List<AdminLembreteDto> lembretes) {
    public static AdminUsuarioDto from(Usuario usuario, List<AdminMedicamentoDto> medicamentos,
                                       List<AdminLembreteDto> lembretes) {
        return new AdminUsuarioDto(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getDataNascimento(), usuario.getComorbidade(), usuario.getRole(), medicamentos, lembretes);
    }
}
