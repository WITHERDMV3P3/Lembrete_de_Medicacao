package com.example.lembretedemedicao.bancodedados;

public class medicamento {
    private int id;
    private String nomemedicamento;
    private String horario;

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomemedicamento() {
        return nomemedicamento;
    }

    public void setNomemedicamento(String nomemedicamento) {
        this.nomemedicamento = nomemedicamento;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

}
