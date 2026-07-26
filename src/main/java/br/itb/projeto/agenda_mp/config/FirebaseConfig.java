package br.itb.projeto.agenda_mp.config;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {

        try {

            String credentials = System.getenv("FIREBASE_CREDENTIALS");

            if (credentials == null || credentials.isBlank()) {
                throw new IllegalStateException(
                        "Variável FIREBASE_CREDENTIALS não encontrada.");
            }

            InputStream serviceAccount = new ByteArrayInputStream(
                    credentials.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível inicializar o Firebase Admin.", e);
        }
    }
}
