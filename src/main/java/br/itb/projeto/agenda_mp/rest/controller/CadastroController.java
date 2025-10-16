package br.itb.projeto.agenda_mp.rest.controller;

import br.itb.projeto.agenda_mp.model.entity.Cadastro;
import br.itb.projeto.agenda_mp.rest.dto.UpdateProfileRequest;
import br.itb.projeto.agenda_mp.service.CadastroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cadastros")
public class CadastroController {
    
    @Autowired
    private CadastroService cadastroService;
    
    @GetMapping
    public List<Cadastro> findAll() {
        return cadastroService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Cadastro> findById(@PathVariable Long id) {
        Optional<Cadastro> cadastro = cadastroService.findById(id);
        return cadastro.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Cadastro create(@RequestBody Cadastro cadastro) {
        return cadastroService.save(cadastro);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Cadastro> update(@PathVariable Long id, @RequestBody Cadastro cadastro) {
        if (cadastroService.findById(id).isPresent()) {
            cadastro.setId(id);
            return ResponseEntity.ok(cadastroService.save(cadastro));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (cadastroService.findById(id).isPresent()) {
            cadastroService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }


        @PutMapping("/{id}/profile")
    public ResponseEntity<String> updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest request) {
        try {

            System.out.println("ID USUARIO " + id);
            boolean success = cadastroService.updateProfile(id, request.getNome(), request.getSenhaAtual(), request.getNovaSenha());
            if (success) {
                return ResponseEntity.ok("Perfil atualizado com sucesso!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Senha atual incorreta ou usuário não encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor");
        }
    }
}