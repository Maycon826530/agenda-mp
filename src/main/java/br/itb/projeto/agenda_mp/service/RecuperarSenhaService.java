package br.itb.projeto.agenda_mp.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.itb.projeto.agenda_mp.model.entity.RecuperarSenha;
import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.model.repository.RecuperarSenhaRepository;
import br.itb.projeto.agenda_mp.model.repository.UsuarioRepository;

@Service
public class RecuperarSenhaService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RecuperarSenhaRepository recuperarSenhaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public RecuperarSenhaService(RecuperarSenhaRepository recuperarSenhaRepository,
            UsuarioRepository usuarioRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.recuperarSenhaRepository = recuperarSenhaRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void solicitarCodigo(String email) {
        usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String codigo = gerarCodigo();
        LocalDateTime agora = LocalDateTime.now();

        RecuperarSenha recuperarSenha = new RecuperarSenha();
        recuperarSenha.setEmail(email);
        recuperarSenha.setCodigo(codigo);
        recuperarSenha.setGeradoEm(agora);
        recuperarSenha.setExepiraEm(agora.plusMinutes(15));
        recuperarSenha.setStatusCodigo(true);

        recuperarSenhaRepository.save(recuperarSenha);
        emailService.enviarCodigo(email, codigo);
    }

    public void redefinirSenha(String email, String codigo, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new RuntimeException("A nova senha deve ter pelo menos 6 caracteres.");
        }

        RecuperarSenha registro = recuperarSenhaRepository.findByEmailAndCodigoAndStatusCodigoTrue(email, codigo)
                .orElseThrow(() -> new RuntimeException("Código inválido ou expirado."));

        if (registro.getExepiraEm().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Código expirado.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        registro.setStatusCodigo(false);
        recuperarSenhaRepository.save(registro);
    }

    public boolean validarCodigo(String email, String codigo) {
        return recuperarSenhaRepository.findByEmailAndCodigoAndStatusCodigoTrue(email, codigo)
                .filter(r -> r.getExepiraEm().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    private String gerarCodigo() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }
}
