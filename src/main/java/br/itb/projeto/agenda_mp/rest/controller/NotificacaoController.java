package br.itb.projeto.agenda_mp.rest.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessagingException;

import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.model.repository.UsuarioRepository;
import br.itb.projeto.agenda_mp.service.NotificacaoService;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.notifications.zone}")
    private String notificationTimeZone;

    public NotificacaoController(
            NotificacaoService notificacaoService,
            UsuarioRepository usuarioRepository) {
        this.notificacaoService = notificacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/status")
    public ResponseEntity<?> status(Authentication authentication) {
        Usuario usuario = getAuthenticatedUser(authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Usuário não autenticado."));
        }

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("tipoNotificacao", usuario.getTipoNotificacao());
        status.put("tokenConfigurado",
                usuario.getFcmToken() != null && !usuario.getFcmToken().isBlank());
        status.put("firebaseInicializado", !FirebaseApp.getApps().isEmpty());
        status.put("fusoHorario", notificationTimeZone);
        status.put("dataHoraServidor",
                LocalDateTime.now(ZoneId.of(notificationTimeZone)).withNano(0).toString());
        return ResponseEntity.ok(status);
    }

    @PostMapping("/teste")
    public ResponseEntity<?> teste(Authentication authentication) {
        Usuario usuario = getAuthenticatedUser(authentication);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("erro", "Usuário não autenticado."));
        }

        if (usuario.getFcmToken() == null || usuario.getFcmToken().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "O usuário não possui token FCM salvo."));
        }

        try {
            String messageId = notificacaoService.enviar(
                    usuario.getFcmToken(),
                    "Teste do PharmaLife",
                    "As notificações pelo sistema estão funcionando."
            );
            return ResponseEntity.ok(Map.of(
                    "enviado", true,
                    "messageId", messageId
            ));
        } catch (FirebaseMessagingException exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "erro", "O Firebase recusou o envio.",
                    "codigo", String.valueOf(exception.getMessagingErrorCode())
            ));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Não foi possível enviar a notificação."));
        }
    }

    private Usuario getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Long usuarioId)) {
            return null;
        }
        return usuarioRepository.findById(usuarioId).orElse(null);
    }
}
