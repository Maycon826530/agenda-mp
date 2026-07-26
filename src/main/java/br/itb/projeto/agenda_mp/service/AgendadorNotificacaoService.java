package br.itb.projeto.agenda_mp.service;

import java.time.LocalTime;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.itb.projeto.agenda_mp.model.entity.Agenda;
import br.itb.projeto.agenda_mp.model.repository.AgendaRepository;

@Service
public class AgendadorNotificacaoService {

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @PostConstruct
    public void teste() {
        System.out.println("SERVIÇO DO SCHEDULER CARREGADO");
    }

    @Scheduled(cron = "0 * * * * *")
    public void verificarMedicamentos() {
        LocalTime agora = LocalTime.now()
                .withSecond(0)
                .withNano(0);

        System.out.println("SCHEDULER RODANDO - Verificando horário: " + agora);

        try {
            List<Agenda> agendas = agendaRepository.findAll().stream()
                    .filter(agenda -> agenda.getHorario() != null)
                    .filter(agenda -> agenda.getHorario()
                            .withSecond(0)
                            .withNano(0)
                            .equals(agora))
                    .toList();

            for (Agenda agenda : agendas) {
                String tipoNotificacao = agenda.getUsuario().getTipoNotificacao();
                if ("browser".equalsIgnoreCase(tipoNotificacao)) {
                    continue;
                }

                String token = agenda.getUsuario().getFcmToken();
                if (token == null || token.isBlank()) {
                    System.out.println("Usuário sem token FCM. Agenda: " + agenda.getId());
                    continue;
                }

                try {
                    String messageId = notificacaoService.enviar(
                            token,
                            "Hora do medicamento!",
                            "Está na hora de tomar " + agenda.getNome()
                    );
                    System.out.println("Notificação enviada! messageId=" + messageId);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
