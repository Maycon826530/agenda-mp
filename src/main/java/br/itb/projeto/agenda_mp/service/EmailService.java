package br.itb.projeto.agenda_mp.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Service
public class EmailService {

    private static final String BREVO_EMAIL_ENDPOINT = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient;
    private final String apiKey;
    private final String senderEmail;
    private final String senderName;

    public EmailService(RestClient.Builder restClientBuilder,
            @Value("${brevo.api-key:}") String apiKey,
            @Value("${brevo.sender-email:}") String senderEmail,
            @Value("${brevo.sender-name:Agenda de Medicamentos Pessoais}") String senderName) {
        this.restClient = restClientBuilder.build();
        this.apiKey = apiKey;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
    }

    public void enviarCodigo(String destino, String codigo) {
        validarConfiguracao();

        Map<String, Object> mensagem = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", destino)),
                "subject", "Código de recuperação de senha",
                "textContent", "Seu código de recuperação é: " + codigo
                        + ".\nEste código expira em 15 minutos.\nNão responda este e-mail.");

        restClient.post()
                .uri(BREVO_EMAIL_ENDPOINT)
                .header("api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(mensagem)
                .retrieve()
                .toBodilessEntity();
    }

    private void validarConfiguracao() {
        if (!StringUtils.hasText(apiKey) || !StringUtils.hasText(senderEmail)) {
            throw new IllegalStateException(
                    "Configure BREVO_API_KEY e BREVO_SENDER_EMAIL para enviar e-mails.");
        }
    }
}
