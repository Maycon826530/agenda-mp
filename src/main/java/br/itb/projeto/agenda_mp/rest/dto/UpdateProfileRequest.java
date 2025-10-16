package br.itb.projeto.agenda_mp.rest.dto;

public class UpdateProfileRequest {
    private String nome;
    private String senhaAtual;
    private String novaSenha;
    
    public UpdateProfileRequest() {}
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getSenhaAtual() { return senhaAtual; }
    public void setSenhaAtual(String senhaAtual) { this.senhaAtual = senhaAtual; }
    
    public String getNovaSenha() { return novaSenha; }
    public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }
}