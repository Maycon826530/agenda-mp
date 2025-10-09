package br.itb.projeto.agenda_mp.rest.controller;

import br.itb.projeto.agenda_mp.model.entity.Horario;
import br.itb.projeto.agenda_mp.service.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {
    
    @Autowired
    private HorarioService horarioService;
    
    @GetMapping
    public List<Horario> findAll() {
        return horarioService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Horario> findById(@PathVariable Long id) {
        Optional<Horario> horario = horarioService.findById(id);
        return horario.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Horario create(@RequestBody Horario horario) {
        return horarioService.save(horario);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Horario> update(@PathVariable Long id, @RequestBody Horario horario) {
        if (horarioService.findById(id).isPresent()) {
            horario.setId(id);
            return ResponseEntity.ok(horarioService.save(horario));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (horarioService.findById(id).isPresent()) {
            horarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}