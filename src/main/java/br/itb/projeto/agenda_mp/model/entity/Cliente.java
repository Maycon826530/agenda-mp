package br.itb.projeto.agenda_mp.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Cliente")
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false)
    private LocalDateTime dataNascimento;
    
    public Cliente() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDateTime getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDateTime dataNascimento) { this.dataNascimento = dataNascimento; }
}