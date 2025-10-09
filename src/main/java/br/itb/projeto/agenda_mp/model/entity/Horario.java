package br.itb.projeto.agenda_mp.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "Horario")
public class Horario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime data;
    
    @Column(nullable = false)
    private LocalTime hora;
    
    @Column(nullable = false, length = 20)
    private String status;
    
    public Horario() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
    
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}