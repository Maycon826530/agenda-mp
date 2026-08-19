package br.itb.projeto.agenda_mp.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recuperar_senha", schema = "dbo")
public class RecuperarSenha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String codigo;

    @Column(name = "gerado_em")
    private LocalDateTime geradoEm;

    @Column(name = "exepira_em")
    private LocalDateTime exepiraEm;

    @Column(name = "status_codigo")
    private Boolean statusCodigo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDateTime getGeradoEm() {
        return geradoEm;
    }

    public void setGeradoEm(LocalDateTime geradoEm) {
        this.geradoEm = geradoEm;
    }

    public LocalDateTime getExepiraEm() {
        return exepiraEm;
    }

    public void setExepiraEm(LocalDateTime exepiraEm) {
        this.exepiraEm = exepiraEm;
    }

    public Boolean isStatusCodigo() {
        return statusCodigo;
    }

    public void setStatusCodigo(Boolean statusCodigo) {
        this.statusCodigo = statusCodigo;
    }

}
