package br.itb.projeto.agenda_mp.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    @Scheduled(cron = "0 * * * * *")
    public void verificarMedicamentos() {

        LocalDateTime agora = LocalDateTime.now()
                .withSecond(0)
                .withNano(0);
        LocalTime horarioAtual = agora.toLocalTime();

        try {

            List<Agenda> agendas =
                    agendaRepository.findByHorarioAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                            horarioAtual,
                            agora,
                            agora
                    );

            for (Agenda agenda : agendas) {

                String tipoNotificacao = agenda.getUsuario().getTipoNotificacao();
                if ("browser".equalsIgnoreCase(tipoNotificacao)) {
                    continue;
                }

                String token = agenda.getUsuario().getFcmToken();

                if (token != null && !token.isBlank()) {

                    try {

                        notificacaoService.enviar(
                                token,
                                "Hora do medicamento!",
                                "Está na hora de tomar " + agenda.getNome()
                        );

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
