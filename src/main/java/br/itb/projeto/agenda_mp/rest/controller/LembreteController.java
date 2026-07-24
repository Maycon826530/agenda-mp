package br.itb.projeto.agenda_mp.rest.controller;

import br.itb.projeto.agenda_mp.model.entity.Lembrete;
import br.itb.projeto.agenda_mp.rest.dto.LembreteRequest;
import br.itb.projeto.agenda_mp.service.LembreteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/lembretes")
public class LembreteController {

    private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm")
            .optionalStart().appendPattern(":ss").optionalEnd()
            .toFormatter();

    @Autowired
    private LembreteService lembreteService;

    @GetMapping
    public List<Lembrete> findByUsuario(@PathVariable Long usuarioId) {
        return lembreteService.findByUsuarioId(usuarioId);
    }

    @PostMapping
    public ResponseEntity<?> create(
            @PathVariable Long usuarioId,
            @RequestBody LembreteRequest request) {
        try {
            return ResponseEntity.ok(lembreteService.save(toLembrete(request), usuarioId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long usuarioId,
            @PathVariable Long id) {
        return lembreteService.deleteByIdAndUsuarioId(id, usuarioId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private Lembrete toLembrete(LembreteRequest request) {
        if (request.getTitulo() == null || request.getTitulo().isBlank()
                || request.getData() == null || request.getData().isBlank()
                || request.getHorario() == null || request.getHorario().isBlank()) {
            throw new IllegalArgumentException("Título, data e horário são obrigatórios.");
        }

        Lembrete lembrete = new Lembrete();
        lembrete.setTitulo(request.getTitulo().trim());
        lembrete.setDescricao(request.getDescricao() == null ? "" : request.getDescricao().trim());
        lembrete.setData(LocalDate.parse(request.getData()));
        lembrete.setHorario(LocalTime.parse(request.getHorario(), TIME_FORMATTER));
        return lembrete;
    }
}
