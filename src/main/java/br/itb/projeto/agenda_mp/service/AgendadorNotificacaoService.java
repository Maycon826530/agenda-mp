package br.itb.projeto.agenda_mp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AgendadorNotificacaoService.class);
    private static final int INITIAL_LOOKBACK_MINUTES = 10;
    private static final int MAX_LOOKBACK_MINUTES = 60;

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private LembreteRepository lembreteRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Value("${app.notifications.zone}")
    private String notificationTimeZone;

    private final Map<String, LocalDateTime> notificacoesEnviadas =
            new ConcurrentHashMap<>();
    private LocalDateTime ultimaVerificacao;

    @Scheduled(fixedDelay = 15000, initialDelay = 5000)
    public synchronized void verificarNotificacoes() {
        LocalDateTime agora = LocalDateTime.now(ZoneId.of(notificationTimeZone))
                .withNano(0);
        LocalDateTime limite = agora.minusMinutes(MAX_LOOKBACK_MINUTES);
        LocalDateTime inicio = ultimaVerificacao == null
                ? agora.minusMinutes(INITIAL_LOOKBACK_MINUTES)
                : ultimaVerificacao.minusSeconds(1);

        if (inicio.isBefore(limite) || inicio.isAfter(agora)) {
            inicio = limite;
        }

        LOGGER.info("Verificando notificacoes entre {} e {}", inicio, agora);

        try {
            verificarMedicamentos(inicio, agora);
            verificarLembretes(inicio, agora);
            ultimaVerificacao = agora;
            limparDeduplicacao(agora.minusDays(1));
        } catch (Exception exception) {
            LOGGER.error("Falha ao verificar notificacoes", exception);
        }
    }

    private void verificarMedicamentos(LocalDateTime inicio, LocalDateTime agora) {
        List<Agenda> agendas =
                agendaRepository.findAgendasAtivasParaNotificacao(inicio, agora);

        for (Agenda agenda : agendas) {
            for (LocalDate data = inicio.toLocalDate();
                    !data.isAfter(agora.toLocalDate());
                    data = data.plusDays(1)) {
                LocalDateTime horarioAgendado =
                        LocalDateTime.of(data, agenda.getHorario());

                if (!estaNaJanela(horarioAgendado, inicio, agora)
                        || horarioAgendado.isBefore(agenda.getDataInicio())
                        || horarioAgendado.isAfter(agenda.getDataFim())) {
                    continue;
                }

                enviarUmaVez(
                        "medicamento|" + agenda.getId() + "|" + horarioAgendado,
                        horarioAgendado,
                        agenda.getUsuario(),
                        "Hora do medicamento!",
                        "Está na hora de tomar " + agenda.getNome()
                );
            }
        }
    }

    private void verificarLembretes(LocalDateTime inicio, LocalDateTime agora) {
        List<Lembrete> lembretes = lembreteRepository.findByDataBetween(
                inicio.toLocalDate(),
                agora.toLocalDate()
        );

        for (Lembrete lembrete : lembretes) {
            LocalDateTime horarioAgendado =
                    LocalDateTime.of(lembrete.getData(), lembrete.getHorario());
            if (!estaNaJanela(horarioAgendado, inicio, agora)) {
                continue;
            }

            String corpo = lembrete.getDescricao() == null
                    || lembrete.getDescricao().isBlank()
                    ? "Você tem um lembrete agendado para agora."
                    : lembrete.getDescricao();

            enviarUmaVez(
                    "lembrete|" + lembrete.getId() + "|" + horarioAgendado,
                    horarioAgendado,
                    lembrete.getUsuario(),
                    "Lembrete: " + lembrete.getTitulo(),
                    corpo
            );
        }
    }

    private boolean estaNaJanela(
            LocalDateTime horario,
            LocalDateTime inicio,
            LocalDateTime fim) {
        return horario.isAfter(inicio) && !horario.isAfter(fim);
    }

    private void enviarUmaVez(
            String chave,
            LocalDateTime horario,
            Usuario usuario,
            String titulo,
            String corpo) {
        if (notificacoesEnviadas.containsKey(chave)
                || usuario == null
                || "browser".equalsIgnoreCase(usuario.getTipoNotificacao())
                || usuario.getFcmToken() == null
                || usuario.getFcmToken().isBlank()) {
            return;
        }

        try {
            String messageId =
                    notificacaoService.enviar(usuario.getFcmToken(), titulo, corpo);
            notificacoesEnviadas.put(chave, horario);
            LOGGER.info("Notificacao enviada: chave={}, messageId={}", chave, messageId);
        } catch (Exception exception) {
            LOGGER.error("Falha ao enviar notificacao: chave={}", chave, exception);
        }
    }

    private void limparDeduplicacao(LocalDateTime limite) {
        notificacoesEnviadas.entrySet()
                .removeIf(entry -> entry.getValue().isBefore(limite));
    }
}
