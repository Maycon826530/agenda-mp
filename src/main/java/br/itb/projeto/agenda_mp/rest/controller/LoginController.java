package br.itb.projeto.agenda_mp.rest.controller;

import br.itb.projeto.agenda_mp.model.entity.Login;
import br.itb.projeto.agenda_mp.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logins")
public class LoginController {
    
    @Autowired
    private LoginService loginService;
    
    @GetMapping
    public List<Login> findAll() {
        return loginService.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Login> findById(@PathVariable Long id) {
        Optional<Login> login = loginService.findById(id);
        return login.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Login create(@RequestBody Login login) {
        return loginService.save(login);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Login> update(@PathVariable Long id, @RequestBody Login login) {
        if (loginService.findById(id).isPresent()) {
            login.setId(id);
            return ResponseEntity.ok(loginService.save(login));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (loginService.findById(id).isPresent()) {
            loginService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}