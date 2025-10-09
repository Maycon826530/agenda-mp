package br.itb.projeto.agenda_mp.model.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "Historico")
public class Historico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(nullable = false, length = 100)
    private String dosagem;
    
    @Column(nullable = false, length = 100)
    private String observacoes;
    
    @Column(nullable = false)
    private LocalTime horario;
    
    public Historico() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDosagem() { return dosagem; }
    public void setDosagem(String dosagem) { this.dosagem = dosagem; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }
}