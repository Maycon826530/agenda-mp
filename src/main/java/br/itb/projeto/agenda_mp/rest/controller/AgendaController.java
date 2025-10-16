package br.itb.projeto.agenda_mp.rest.controller;

import br.itb.projeto.agenda_mp.model.entity.Agenda;
import br.itb.projeto.agenda_mp.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agenda")
public class AgendaController {
    
    @Autowired
    private AgendaService agendaService;
    
    @GetMapping
    public List<Agenda> findAll() {
        return agendaService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Agenda> findById(@PathVariable Long id) {
        Optional<Agenda> agenda = agendaService.findById(id);
        return agenda.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Agenda create(@RequestBody Agenda agenda) {
        return agendaService.save(agenda);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Agenda> update(@PathVariable Long id, @RequestBody Agenda agenda) {
        if (agendaService.findById(id).isPresent()) {
            agenda.setId(id);
            return ResponseEntity.ok(agendaService.save(agenda));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (agendaService.findById(id).isPresent()) {
            agendaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}