package br.itb.projeto.agenda_mp.rest.dto;

import br.itb.projeto.agenda_mp.model.entity.Lembrete;
import java.time.LocalDate;
import java.time.LocalTime;

public record AdminLembreteDto(Long id, String titulo, String descricao, LocalDate data, LocalTime horario) {
    public static AdminLembreteDto from(Lembrete lembrete) {
        return new AdminLembreteDto(lembrete.getId(), lembrete.getTitulo(), lembrete.getDescricao(),
                lembrete.getData(), lembrete.getHorario());
    }
}
