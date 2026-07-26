package br.itb.projeto.agenda_mp.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import br.itb.projeto.agenda_mp.model.entity.Agenda;
import br.itb.projeto.agenda_mp.model.entity.Lembrete;
import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.model.repository.AgendaRepository;
import br.itb.projeto.agenda_mp.model.repository.LembreteRepository;

@Service
public class AgendadorNotificacaoService {

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private LembreteRepository lembreteRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Value("${app.notifications.zone}")
    private String notificationTimeZone;

    @Scheduled(cron = "0 * * * * *", zone = "${app.notifications.zone}")
    public void verificarNotificacoes() {
        LocalDateTime agora = LocalDateTime.now(ZoneId.of(notificationTimeZone))
                .withSecond(0)
                .withNano(0);
        LocalTime horarioAtual = agora.toLocalTime();

        try {
            List<Agenda> agendas = agendaRepository.findAgendasAtivasParaNotificacao(
                    horarioAtual,
                    agora
            );

            for (Agenda agenda : agendas) {
                enviarParaUsuario(
                        agenda.getUsuario(),
                        "Hora do medicamento!",
                        "Está na hora de tomar " + agenda.getNome()
                );
            }

            List<Lembrete> lembretes = lembreteRepository.findByDataAndHorario(
                    agora.toLocalDate(),
                    horarioAtual
            );

            for (Lembrete lembrete : lembretes) {
                String corpo = lembrete.getDescricao() == null
                        || lembrete.getDescricao().isBlank()
                        ? "Você tem um lembrete agendado para agora."
                        : lembrete.getDescricao();

                enviarParaUsuario(
                        lembrete.getUsuario(),
                        "Lembrete: " + lembrete.getTitulo(),
                        corpo
                );
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void enviarParaUsuario(Usuario usuario, String titulo, String corpo) {
        if (usuario == null
                || "browser".equalsIgnoreCase(usuario.getTipoNotificacao())
                || usuario.getFcmToken() == null
                || usuario.getFcmToken().isBlank()) {
            return;
        }

        try {
            notificacaoService.enviar(usuario.getFcmToken(), titulo, corpo);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
