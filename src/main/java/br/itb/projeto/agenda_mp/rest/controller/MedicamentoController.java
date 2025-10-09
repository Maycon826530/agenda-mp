package br.itb.projeto.agenda_mp.rest.controller;

import br.itb.projeto.agenda_mp.model.entity.Medicamento;
import br.itb.projeto.agenda_mp.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoController {
    
    @Autowired
    private MedicamentoService medicamentoService;
    
    @GetMapping
    public List<Medicamento> findAll() {
        return medicamentoService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Medicamento> findById(@PathVariable Long id) {
        Optional<Medicamento> medicamento = medicamentoService.findById(id);
        return medicamento.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Medicamento create(@RequestBody Medicamento medicamento) {
        return medicamentoService.save(medicamento);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Medicamento> update(@PathVariable Long id, @RequestBody Medicamento medicamento) {
        if (medicamentoService.findById(id).isPresent()) {
            medicamento.setId(id);
            return ResponseEntity.ok(medicamentoService.save(medicamento));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (medicamentoService.findById(id).isPresent()) {
            medicamentoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}