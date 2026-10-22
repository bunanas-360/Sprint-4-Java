package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "RELATORIO_PRIORIDADE")
@SequenceGenerator(name = "relatorioPrioridadeSeq", sequenceName = "SEQ_RELATORIO_PRIORIDADE", allocationSize = 1)
public class RelatorioPrioridade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "relatorioPrioridadeSeq")
    @Column(name = "ID")
    private Long id;

    @Column(name = "DATA_GERACAO", nullable = false)
    private LocalDateTime dataGeracao;

    @Column(name = "QT_URGENTE", nullable = false, precision = 10, scale = 0)
    private Integer qtUrgente;

    @Column(name = "QT_CRITICO", nullable = false, precision = 10, scale = 0)
    private Integer qtCritico;

    @Column(name = "QT_ATENCAO", nullable = false, precision = 10, scale = 0)
    private Integer qtAtencao;

    @Column(name = "QT_NORMAL", nullable = false, precision = 10, scale = 0)
    private Integer qtNormal;

    @Column(name = "RESUMO", length = 2000)
    private String resumo;

    protected RelatorioPrioridade() {
        // Exigido pelo JPA.
    }

    public RelatorioPrioridade(Integer qtUrgente, Integer qtCritico, Integer qtAtencao,
                               Integer qtNormal, String resumo) {
        this.qtUrgente = qtUrgente;
        this.qtCritico = qtCritico;
        this.qtAtencao = qtAtencao;
        this.qtNormal = qtNormal;
        this.resumo = resumo;
    }

    @PrePersist
    void preencherDataGeracao() {
        if (dataGeracao == null) {
            dataGeracao = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public Integer getQtUrgente() { return qtUrgente; }
    public Integer getQtCritico() { return qtCritico; }
    public Integer getQtAtencao() { return qtAtencao; }
    public Integer getQtNormal() { return qtNormal; }
    public String getResumo() { return resumo; }
}
