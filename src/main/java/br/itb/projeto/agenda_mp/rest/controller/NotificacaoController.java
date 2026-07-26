package br.itb.projeto.agenda_mp.rest.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.itb.projeto.agenda_mp.service.NotificacaoService;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @PostMapping("/teste")
public ResponseEntity<?> teste(@RequestBody Map<String, String> body) {

    try {

        String token = body.get("token");

        String resposta = notificacaoService.enviar(
                token,
                "PharmaLife",
                "Teste de notificação 🚀");

        return ResponseEntity.ok(resposta);

    } catch (Exception e) {

        e.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Não foi possível enviar a notificação.");
    }
}
}
