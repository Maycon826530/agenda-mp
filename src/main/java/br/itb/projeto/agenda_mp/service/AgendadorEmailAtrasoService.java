package br.itb.projeto.agenda_mp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.itb.projeto.agenda_mp.model.entity.Agenda;
import br.itb.projeto.agenda_mp.model.entity.NotificacaoAtraso;
import br.itb.projeto.agenda_mp.model.entity.Usuario;
import br.itb.projeto.agenda_mp.model.repository.AgendaRepository;
import br.itb.projeto.agenda_mp.model.repository.HistoricoRepository;
import br.itb.projeto.agenda_mp.model.repository.NotificacaoAtrasoRepository;

@Service
public class AgendadorEmailAtrasoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgendadorEmailAtrasoService.class);
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final AgendaRepository agendaRepository;
    private final HistoricoRepository historicoRepository;
    private final NotificacaoAtrasoRepository notificacaoAtrasoRepository;
    private final EmailService emailService;
    private final long atrasoMinutos;

    public AgendadorEmailAtrasoService(
            AgendaRepository agendaRepository,
            HistoricoRepository historicoRepository,
            NotificacaoAtrasoRepository notificacaoAtrasoRepository,
            EmailService emailService,
            @Value("${app.notifications.late-email-delay-minutes:60}") long atrasoMinutos) {
        this.agendaRepository = agendaRepository;
        this.historicoRepository = historicoRepository;
        this.notificacaoAtrasoRepository = notificacaoAtrasoRepository;
        this.emailService = emailService;
        this.atrasoMinutos = atrasoMinutos;
    }

    @Scheduled(cron = "${app.notifications.late-email-cron:0 * * * * *}",
            zone = "${app.notifications.zone:America/Sao_Paulo}")
    public void verificarMedicamentosAtrasados() {
        LocalDateTime agora = LocalDateTime.now().withSecond(0).withNano(0);
        List<Agenda> agendas = agendaRepository.findAll();

        for (Agenda agenda : agendas) {
            verificarOcorrencia(agenda, agora.toLocalDate(), agora);
            verificarOcorrencia(agenda, agora.toLocalDate().minusDays(1), agora);
        }
    }

    private void verificarOcorrencia(Agenda agenda, LocalDate data, LocalDateTime agora) {
        if (agenda.getHorario() == null || agenda.getDataInicio() == null || agenda.getDataFim() == null
                || data.isBefore(agenda.getDataInicio().toLocalDate())
                || data.isAfter(agenda.getDataFim().toLocalDate())) {
            return;
        }

        LocalDateTime dataPrevista = LocalDateTime.of(data, agenda.getHorario()).withSecond(0).withNano(0);
        if (agora.isBefore(dataPrevista.plusMinutes(atrasoMinutos))
                || agora.isAfter(dataPrevista.plusDays(1))) {
            return;
        }

        if (notificacaoAtrasoRepository.existsByAgendaIdAndDataPrevista(agenda.getId(), dataPrevista)
                || historicoRepository.existeResolucaoNoPeriodo(
                        agenda.getId(), data.atStartOfDay(), data.plusDays(1).atStartOfDay())) {
            return;
        }

        Usuario usuario = agenda.getUsuario();
        if (usuario == null || !StringUtils.hasText(usuario.getEmail())) {
            LOGGER.warn("Agenda {} sem e-mail de usu\u00e1rio para lembrete de atraso", agenda.getId());
            return;
        }

        try {
            emailService.enviarLembreteMedicamentoAtrasado(
                    usuario.getEmail(), usuario.getNome(), agenda.getNome(), agenda.getDosagem(),
                    agenda.getHorario().format(HORA));

            NotificacaoAtraso notificacao = new NotificacaoAtraso();
            notificacao.setAgenda(agenda);
            notificacao.setDataPrevista(dataPrevista);
            notificacao.setEmailEnviadoEm(agora);
            notificacaoAtrasoRepository.save(notificacao);
            LOGGER.info("E-mail de medicamento atrasado enviado para a agenda {}", agenda.getId());
        } catch (Exception exception) {
            LOGGER.error("Falha ao enviar e-mail de atraso para a agenda {}", agenda.getId(), exception);
        }
    }
}
