package br.itb.projeto.agenda_mp.rest.dto;

public class LembreteRequest {
    private String titulo;
    private String descricao;
    private String data;
    private String horario;

    public LembreteRequest() {}

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
}
