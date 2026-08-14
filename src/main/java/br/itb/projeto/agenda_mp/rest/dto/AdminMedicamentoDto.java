package br.itb.projeto.agenda_mp.rest.dto;

import br.itb.projeto.agenda_mp.model.entity.Medicamento;
import java.time.LocalDateTime;

public record AdminMedicamentoDto(Long id, String nome, String descricao, String tipo,
                                  String complemento, String statusMedicamento,
                                  LocalDateTime dataCadastro) {
    public static AdminMedicamentoDto from(Medicamento medicamento) {
        return new AdminMedicamentoDto(medicamento.getId(), medicamento.getNome(),
                medicamento.getDescricao(), medicamento.getTipo(), medicamento.getComplemento(),
                medicamento.getStatusMedicamento(), medicamento.getDataCadastro());
    }
}
