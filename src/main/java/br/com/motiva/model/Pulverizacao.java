package br.com.motiva.model;

public class Pulverizacao extends IntervencaoOperacional {
    public Pulverizacao() {
        super("Aplicação de produto para controle da vegetação");
    }

    @Override
    public String executarServico() {
        return "Executando pulverização.";
    }
}
