package br.itb.projeto.agenda_mp.rest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.rest.dto.UpdateProfileRequest;
import br.itb.projeto.agenda_mp.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UsuarioController {
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API está funcionando!");
    }
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<Usuario> usuarios = usuarioService.findAll();
            System.out.println("Encontrados " + usuarios.size() + " usuários");
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuários: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar usuários");
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            Optional<Usuario> usuario = usuarioService.findById(id);
            if (usuario.isPresent()) {
                return ResponseEntity.ok(usuario.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar usuário por ID: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor");
        }
    }
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Usuario usuario) {
        try {
            if (usuario.getNome() == null || usuario.getEmail() == null || usuario.getSenha() == null) {
                return ResponseEntity.badRequest().body("Nome, email e senha são obrigatórios");
            }
            
            // Verificar se email já existe
            Optional<Usuario> usuarioExistente = usuarioService.findByEmail(usuario.getEmail());
            if (usuarioExistente.isPresent()) {
                return ResponseEntity.badRequest().body("Email já está em uso");
            }
            
            Usuario novoUsuario = usuarioService.save(usuario);
            return ResponseEntity.ok(novoUsuario);
        } catch (Exception e) {
            System.err.println("Erro ao criar usuário: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar usuário");
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody Usuario usuario) {
        if (usuarioService.findById(id).isPresent()) {
            usuario.setId(id);
            return ResponseEntity.ok(usuarioService.save(usuario));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (usuarioService.findById(id).isPresent()) {
            usuarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String senha) {
        try {
            System.out.println("Tentativa de login para email: " + email);
            
            if (email == null || email.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email e senha são obrigatórios");
            }
            
            Optional<Usuario> usuario = usuarioService.login(email.trim(), senha);
            
            if (usuario.isPresent()) {
                System.out.println("Login bem-sucedido para: " + email);
                return ResponseEntity.ok(usuario.get());
            } else {
                System.out.println("Usuário não encontrado para email: " + email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado ou senha incorreta");
            }
        } catch (Exception e) {
            System.err.println("Erro durante login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor");
        }
    }
    
    @PutMapping("/{id}/profile")
    public ResponseEntity<String> updateProfile(@PathVariable Long id, @RequestBody UpdateProfileRequest request) {
        try {
            boolean success = usuarioService.updateProfile(id, request.getNome(), request.getSenhaAtual(), request.getNovaSenha());
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