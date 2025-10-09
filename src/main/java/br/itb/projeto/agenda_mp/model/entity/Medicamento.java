package br.itb.projeto.agenda_mp.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Medicamento")
public class Medicamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(nullable = false, length = 100)
    private String descricao;
    
    @Column(nullable = false, length = 100)
    private String tipo;
    
    @Column(length = 50)
    private String complemento;
    
    @Column(nullable = false)
    private LocalDateTime dataCadastro;
    
    @Column(nullable = false, length = 20)
    private String statusMedicamento;
    
    public Medicamento() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    
    public String getStatusMedicamento() { return statusMedicamento; }
    public void setStatusMedicamento(String statusMedicamento) { this.statusMedicamento = statusMedicamento; }
}