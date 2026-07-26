package br.itb.projeto.agenda_mp.service;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

@Service
public class NotificacaoService {

    public String enviar(String token, String titulo, String corpo) throws FirebaseMessagingException {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token FCM é obrigatório.");
        }

        Message message = Message.builder()
                .setToken(token)
                .putData("title", titulo)
                .putData("body", corpo)
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }
}
