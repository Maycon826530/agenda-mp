package br.itb.projeto.agenda_mp.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthTokenService {

    private final byte[] secret;
    private final long tokenValiditySeconds;

    public AuthTokenService(
            @Value("${app.auth.secret}") String secret,
            @Value("${app.auth.token-validity-seconds}") long tokenValiditySeconds) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("AUTH_SECRET deve possuir pelo menos 32 caracteres.");
        }
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.tokenValiditySeconds = tokenValiditySeconds;
    }

    public String generate(Long usuarioId) {
        long expiresAt = Instant.now().getEpochSecond() + tokenValiditySeconds;
        String payload = usuarioId + ":" + expiresAt;
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return encodedPayload + "." + sign(encodedPayload);
    }

    public Long validateAndGetUsuarioId(String token) {
        if (token == null) return null;

        String[] parts = token.split("\\.", 2);
        if (parts.length != 2) return null;

        byte[] expected = sign(parts[0]).getBytes(StandardCharsets.UTF_8);
        byte[] provided = parts[1].getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, provided)) return null;

        try {
            String payload = new String(
                    Base64.getUrlDecoder().decode(parts[0]),
                    StandardCharsets.UTF_8
            );
            String[] values = payload.split(":", 2);
            Long usuarioId = Long.valueOf(values[0]);
            long expiresAt = Long.parseLong(values[1]);
            return expiresAt > Instant.now().getEpochSecond() ? usuarioId : null;
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível assinar o token.", exception);
        }
    }
}
