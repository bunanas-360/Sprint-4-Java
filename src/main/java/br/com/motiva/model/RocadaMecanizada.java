package br.com.motiva.model;

public class RocadaMecanizada extends IntervencaoOperacional {
    public RocadaMecanizada() {
        super("Execução de roçada mecanizada em área crítica");
    }

    @Override
    public String executarServico() {
        return "Executando roçada mecanizada.";
    }
}
