package br.itb.projeto.agenda_mp.rest.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.rest.dto.UpdateProfileRequest;
import br.itb.projeto.agenda_mp.service.UsuarioService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final LocalDate ONBOARDING_PENDING_DATE = LocalDate.of(1900, 1, 1);

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("API está funcionando!");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
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

            usuario.setDataNascimento(ONBOARDING_PENDING_DATE);
            usuario.setComorbidade("");

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
        return usuarioService.update(id, usuario)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, @RequestBody(required = false) java.util.Map<String, String> body) {
        String senhaAtual = body != null ? body.get("senhaAtual") : null;

        if (senhaAtual == null || senhaAtual.isBlank()) {
            return ResponseEntity.badRequest().body("Senha obrigatória para excluir a conta");
        }

        boolean excluido = usuarioService.deleteComValidacao(id, senhaAtual);
        if (excluido) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha incorreta ou usuário não encontrado");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario _usuario) {
        try {
            System.out.println("Tentativa de login para email: " + _usuario.getEmail());

            if (_usuario.getEmail() == null || _usuario.getEmail().trim().isEmpty() ||
                _usuario.getSenha() == null || _usuario.getSenha().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email e senha são obrigatórios");
            }

            Optional<Usuario> usuario = usuarioService.login(_usuario.getEmail().trim(), _usuario.getSenha());

            if (usuario.isPresent()) {
                System.out.println("Login bem-sucedido para: " + _usuario.getEmail());
                return ResponseEntity.ok(usuario.get());
            } else {
                System.out.println("Usuário não encontrado para email: " + _usuario.getEmail());
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

@PostMapping("/google-login")
public ResponseEntity<?> googleLogin(@RequestBody java.util.Map<String, String> body) {

    try {

        String token = body.get("token");

        FirebaseToken decodedToken =
                FirebaseAuth.getInstance().verifyIdToken(token);

        String email = decodedToken.getEmail();
        String nome = (String) decodedToken.getClaims().getOrDefault("name", "Usuário Google");
        String foto = (String) decodedToken.getClaims().get("picture");

        Optional<Usuario> usuarioExistente =
                usuarioService.findByEmail(email);

        Usuario usuario;

        if (usuarioExistente.isPresent()) {

    usuario = usuarioExistente.get();

    // Atualiza a foto
    usuario.setFoto(foto);

    usuario = usuarioService.save(usuario);

} else {

    usuario = new Usuario();
    usuario.setNome(nome);
    usuario.setEmail(email);
    usuario.setFoto(foto);

usuario.setDataNascimento(ONBOARDING_PENDING_DATE);    usuario.setComorbidade("");

    usuario.setSenha(java.util.UUID.randomUUID().toString());

    usuario = usuarioService.save(usuario);
}

        return ResponseEntity.ok(usuario);

    } catch (Exception e) {

        System.out.println("========== ERRO FIREBASE ==========");
        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("ERRO: " + e.getMessage());
    }
}

    @GetMapping("/{id}/profile-status")
public ResponseEntity<?> getProfileStatus(@PathVariable Long id) {
    Optional<Usuario> usuarioOpt = usuarioService.findById(id);

    if (usuarioOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    boolean perfilCompleto = usuarioService.perfilCompleto(usuarioOpt.get());
    return ResponseEntity.ok(Map.of("perfilCompleto", perfilCompleto));
}

    @PutMapping("/{id}/onboarding")
public ResponseEntity<?> finalizarOnboarding(@PathVariable Long id, @RequestBody Map<String, String> body) {
    try {
        String nome = body.get("nome");
        String dataNascimento = body.get("dataNascimento");
        String comorbidade = body.get("comorbidade");

        Optional<Usuario> usuarioAtualizado = usuarioService.finalizarOnboarding(id, nome, dataNascimento, comorbidade);

        if (usuarioAtualizado.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Dados obrigatorios do onboarding invalidos");
        }

        return ResponseEntity.ok(usuarioAtualizado.get());
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao finalizar onboarding");
    }
}

    @PutMapping("/{id}/fcm-token")
public ResponseEntity<?> atualizarFcmToken(
        @PathVariable Long id,
        @RequestBody java.util.Map<String, String> body) {

    Optional<Usuario> usuarioOpt = usuarioService.findById(id);

    if (usuarioOpt.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    Usuario usuario = usuarioOpt.get();
    usuario.setFcmToken(body.get("token"));

    usuarioService.save(usuario);

    return ResponseEntity.ok("Token salvo.");
}

    @PutMapping("/{id}/tipo-notificacao")
public ResponseEntity<?> atualizarTipoNotificacao(
        @PathVariable Long id,
        @RequestBody java.util.Map<String, String> body) {

    Optional<Usuario> usuarioAtualizado =
            usuarioService.atualizarTipoNotificacao(id, body.get("tipoNotificacao"));

    if (usuarioAtualizado.isEmpty()) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(usuarioAtualizado.get());
}
}


    
