package br.itb.projeto.agenda_mp.rest.controller;


import br.itb.projeto.agenda_mp.service.RecuperarSenhaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.itb.projeto.agenda_mp.rest.dto.RedefinirSenhaDTO;

@RestController
@RequestMapping("/recuperar-senha")
public class RecuperarSenhaController {

    private final RecuperarSenhaService recuperarSenhaService;

    public RecuperarSenhaController(RecuperarSenhaService recuperarSenhaService) {
        this.recuperarSenhaService = recuperarSenhaService;
    }

    @PostMapping("/solicitar-codigo")
    public ResponseEntity<String> solicitarCodigo(@RequestParam String email) {
        try {
            recuperarSenhaService.solicitarCodigo(email);
            return ResponseEntity.ok("Código enviado para o e-mail informado.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/validar-codigo")
    public ResponseEntity<String> validarCodigo(@RequestParam String email, @RequestParam String codigo) {
        boolean valido = recuperarSenhaService.validarCodigo(email, codigo);
        if (valido) {
            return ResponseEntity.ok("Código válido.");
        } else {
            return ResponseEntity.badRequest().body("Código inválido ou expirado.");
        }
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<String> redefinirSenha(@RequestBody RedefinirSenhaDTO dto) {
        try {
            recuperarSenhaService.redefinirSenha(dto.getEmail(), dto.getCodigo(), dto.getNovaSenha());
            return ResponseEntity.ok("Senha redefinida com sucesso.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
