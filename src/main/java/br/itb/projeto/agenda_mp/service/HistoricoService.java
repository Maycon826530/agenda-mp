package br.itb.projeto.agenda_mp.service;

import br.itb.projeto.agenda_mp.model.entity.Agenda;
import br.itb.projeto.agenda_mp.model.entity.Historico;
import br.itb.projeto.agenda_mp.model.entity.Medicamento;
import br.itb.projeto.agenda_mp.model.repository.AgendaRepository;
import br.itb.projeto.agenda_mp.model.repository.HistoricoRepository;
import br.itb.projeto.agenda_mp.model.repository.MedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricoService {

    @Autowired
    private HistoricoRepository historicoRepository;

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public List<Historico> findAll() {
        return historicoRepository.findAll();
    }

    public List<Historico> findByAgendaId(Long agendaId) {
        return historicoRepository.findByAgendaId(agendaId);
    }

    public List<Historico> findByUsuarioId(Long usuarioId) {
        return historicoRepository.findByAgendaUsuarioId(usuarioId);
    }

    public Optional<Historico> findById(Long id) {
        return historicoRepository.findById(id);
    }

    public Historico registrar(Historico historico, Long agendaId, Long medicamentoId) {
        Agenda agenda = agendaRepository.findById(agendaId)
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada: " + agendaId));
        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento não encontrado: " + medicamentoId));
        if (medicamento.getAgenda() == null || !agendaId.equals(medicamento.getAgenda().getId())) {
            throw new IllegalArgumentException("Medicamento não pertence à agenda informada.");
        }
        historico.setAgenda(agenda);
        historico.setMedicamento(medicamento);
        historico.setStatus(normalizarStatus(historico.getStatus()));
        // Garante horario não nulo
        if (historico.getHorario() == null) {
            historico.setHorario(LocalTime.now());
        }
        // Garante observacoes não nulo
        if (historico.getObservacoes() == null) {
            historico.setObservacoes("");
        }
        return historicoRepository.save(historico);
    }

    // Confirma o uso do medicamento via notificação
    public Historico confirmarUso(Long historicoId) {
        Historico historico = historicoRepository.findById(historicoId)
                .orElseThrow(() -> new IllegalArgumentException("Histórico não encontrado: " + historicoId));
        historico.setStatus("TOMADO");
        historico.setDataConfirmacao(LocalDateTime.now());
        return historicoRepository.save(historico);
    }

    // Registra que o medicamento foi ignorado no horário
    public Historico ignorarUso(Long historicoId, String motivoIgnorado) {
        Historico historico = historicoRepository.findById(historicoId)
                .orElseThrow(() -> new IllegalArgumentException("Histórico não encontrado: " + historicoId));
        historico.setStatus("IGNORADO");
        historico.setMotivoIgnorado(
                motivoIgnorado == null || motivoIgnorado.isBlank()
                        ? "Outro motivo"
                        : motivoIgnorado.trim()
        );
        historico.setDataHoraIgnorado(LocalDateTime.now());
        return historicoRepository.save(historico);
    }

    private String normalizarStatus(String status) {
        if (status == null || status.isBlank()) return "PENDENTE";

        return switch (status.trim().toUpperCase()) {
            case "TOMADO", "CONFIRMADO" -> "TOMADO";
            case "PERDIDO" -> "PERDIDO";
            case "IGNORADO" -> "IGNORADO";
            default -> "PENDENTE";
        };
    }

    public void deleteById(Long id) {
        historicoRepository.deleteById(id);
    }
}
