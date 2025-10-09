package br.itb.projeto.agenda_mp.rest.controller;

import br.itb.projeto.agenda_mp.model.entity.Historico;
import br.itb.projeto.agenda_mp.service.HistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/historicos")
public class HistoricoController {
    
    @Autowired
    private HistoricoService historicoService;
    
    @GetMapping
    public List<Historico> findAll() {
        return historicoService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Historico> findById(@PathVariable Long id) {
        Optional<Historico> historico = historicoService.findById(id);
        return historico.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Historico create(@RequestBody Historico historico) {
        return historicoService.save(historico);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Historico> update(@PathVariable Long id, @RequestBody Historico historico) {
        if (historicoService.findById(id).isPresent()) {
            historico.setId(id);
            return ResponseEntity.ok(historicoService.save(historico));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (historicoService.findById(id).isPresent()) {
            historicoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}