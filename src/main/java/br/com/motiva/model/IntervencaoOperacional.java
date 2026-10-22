package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/** Base de domínio herdada das intervenções polimórficas da Sprint 2. */
@MappedSuperclass
public abstract class IntervencaoOperacional {

    @Column(name = "DESCRICAO", nullable = false, length = 500)
    protected String descricao;

    protected IntervencaoOperacional() {
        // Construtor protegido para suporte à herança/JPA.
    }

    protected IntervencaoOperacional(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    /** Retorna uma descrição do serviço sem usar o console como interface da aplicação. */
    public abstract String executarServico();
}
