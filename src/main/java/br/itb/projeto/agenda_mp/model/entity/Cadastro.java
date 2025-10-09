package br.itb.projeto.agenda_mp.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Cadastro")
public class Cadastro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(nullable = false)
    private Integer idade;
    
    @Column(length = 100)
    private String comorbidade;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false, length = 100)
    private String senha;
    
    public Cadastro() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
    
    public String getComorbidade() { return comorbidade; }
    public void setComorbidade(String comorbidade) { this.comorbidade = comorbidade; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}