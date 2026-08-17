package br.itb.projeto.agenda_mp.service;

import java.time.LocalDateTime;
import java.security.SecureRandom;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.itb.projeto.agenda_mp.model.entity.RecuperarSenha;
import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.model.repository.RecuperarSenhaRepository;
import br.itb.projeto.agenda_mp.model.repository.UsuarioRepository;

@Service
public class RecuperarSenhaService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private RecuperarSenhaRepository recuperarSenhaRepository;
    private UsuarioRepository usuarioRepository;
    private JavaMailSender mailSender;
    private PasswordEncoder passwordEncoder;

    public RecuperarSenhaService(RecuperarSenhaRepository recuperarSenhaRepository, UsuarioRepository usuarioRepository,
            JavaMailSender mailSender, PasswordEncoder passwordEncoder) {
        this.recuperarSenhaRepository = recuperarSenhaRepository;
        this.usuarioRepository = usuarioRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    public void solicitarCodigo(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("UsuÃ¡rio nÃ£o encontrado"));

        if (usuario != null) {
            String codigo = gerarCodigo();
            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime expiracao = agora.plusMinutes(15);

            RecuperarSenha recuperarSenha = new RecuperarSenha();
            recuperarSenha.setEmail(email);
            recuperarSenha.setCodigo(codigo);
            recuperarSenha.setGeradoEm(agora);
            recuperarSenha.setExepiraEm(expiracao);
            recuperarSenha.setStatusCodigo(true);

            recuperarSenhaRepository.save(recuperarSenha);

            enviarCodigo(email, codigo);
        }
    }

    public void redefinirSenha(String email, String codigo, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new RuntimeException("A nova senha deve ter pelo menos 6 caracteres.");
        }
        RecuperarSenha registro = recuperarSenhaRepository.findByEmailAndCodigoAndStatusCodigoTrue(email, codigo)
                .orElseThrow(() -> new RuntimeException("CÃ³digo invÃ¡lido ou expirado."));

        if (registro.getExepiraEm().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("CÃ³digo expirado.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("UsuÃ¡rio nÃ£o encontrado."));

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

    private void enviarCodigo(String email, String codigo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(email);
        mensagem.setSubject("CÃ³digo de RecuperaÃ§Ã£o de senha");
        mensagem.setText("Seu cÃ³digo de recuperaÃ§Ã£o Ã©: " + codigo
                + ".\n" + "Este cÃ³digo expira em 15 minutos."
                + "\n" + "NÃ£o responda esse e-mail.");

        mailSender.send(mensagem);
    }
}
