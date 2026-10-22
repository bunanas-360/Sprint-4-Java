package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/** Entidade de persistência para a tabela INTERVENCAO_OPERACIONAL da Sprint 3. */
@Entity
@Table(name = "INTERVENCAO_OPERACIONAL")
@SequenceGenerator(name = "intervencaoSeq", sequenceName = "SEQ_INTERVENCAO_OPERACIONAL", allocationSize = 1)
public class IntervencaoOperacionalEntity extends IntervencaoOperacional {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "intervencaoSeq")
    @Column(name = "ID")
    private Long id;

    @Column(name = "TIPO", nullable = false, length = 100)
    private String tipo;

    protected IntervencaoOperacionalEntity() {
        // Exigido pelo JPA.
    }

    public IntervencaoOperacionalEntity(String tipo, String descricao) {
        super(descricao);
        this.tipo = tipo;
    }

    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String executarServico() {
        return "Executando intervenção: " + tipo + ".";
    }
}
