package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "TRECHO_RODOVIA")
@SequenceGenerator(name = "trechoRodoviaSeq", sequenceName = "SEQ_TRECHO_RODOVIA", allocationSize = 1)
public class TrechoRodovia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trechoRodoviaSeq")
    @Column(name = "ID")
    private Long id;

    @Column(name = "KM", nullable = false, precision = 10, scale = 0)
    private Integer km;

    @Column(name = "CRESCIMENTO_VEGETACAO", nullable = false, precision = 10, scale = 2)
    private BigDecimal crescimentoVegetacao;

    @Column(name = "TIPO_TRECHO", nullable = false, length = 50)
    private String tipoTrecho;

    protected TrechoRodovia() {
        // Exigido pelo JPA.
    }

    public TrechoRodovia(Integer km, Double crescimentoVegetacao, String tipoTrecho) {
        this.km = km;
        this.crescimentoVegetacao = crescimentoVegetacao == null ? null : BigDecimal.valueOf(crescimentoVegetacao);
        this.tipoTrecho = tipoTrecho;
    }

    public Long getId() { return id; }
    public Integer getKm() { return km; }
    public BigDecimal getCrescimentoVegetacao() { return crescimentoVegetacao; }
    public String getTipoTrecho() { return tipoTrecho; }

    public void setKm(Integer km) { this.km = km; }
    public void setCrescimentoVegetacao(Double crescimentoVegetacao) {
        this.crescimentoVegetacao = crescimentoVegetacao == null ? null : BigDecimal.valueOf(crescimentoVegetacao);
    }
    public void setTipoTrecho(String tipoTrecho) { this.tipoTrecho = tipoTrecho; }

    public void atualizarCrescimento() {
        if ("umido".equalsIgnoreCase(tipoTrecho)) {
            crescimentoVegetacao = crescimentoVegetacao.add(BigDecimal.valueOf(15));
        } else {
            crescimentoVegetacao = crescimentoVegetacao.add(BigDecimal.valueOf(5));
        }
    }
}
