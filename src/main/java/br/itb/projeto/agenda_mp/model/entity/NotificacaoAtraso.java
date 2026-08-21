package br.itb.projeto.agenda_mp.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Notificacao_Atraso", uniqueConstraints = {
        @UniqueConstraint(name = "UQ_Notificacao_Atraso_Agenda_Data", columnNames = {"agenda_id", "data_prevista"})
})
public class NotificacaoAtraso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agenda_id", nullable = false)
    private Agenda agenda;

    @Column(name = "data_prevista", nullable = false)
    private LocalDateTime dataPrevista;

    @Column(name = "email_enviado_em", nullable = false)
    private LocalDateTime emailEnviadoEm;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Agenda getAgenda() { return agenda; }
    public void setAgenda(Agenda agenda) { this.agenda = agenda; }
    public LocalDateTime getDataPrevista() { return dataPrevista; }
    public void setDataPrevista(LocalDateTime dataPrevista) { this.dataPrevista = dataPrevista; }
    public LocalDateTime getEmailEnviadoEm() { return emailEnviadoEm; }
    public void setEmailEnviadoEm(LocalDateTime emailEnviadoEm) { this.emailEnviadoEm = emailEnviadoEm; }
}
