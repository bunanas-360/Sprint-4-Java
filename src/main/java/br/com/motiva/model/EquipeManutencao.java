package br.com.motiva.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "EQUIPE_MANUTENCAO")
@SequenceGenerator(name = "equipeManutencaoSeq", sequenceName = "SEQ_EQUIPE_MANUTENCAO", allocationSize = 1)
public class EquipeManutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipeManutencaoSeq")
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOME_EQUIPE", nullable = false, length = 100)
    private String nomeEquipe;

    @Column(name = "RESPONSAVEL", nullable = false, length = 100)
    private String responsavel;

    protected EquipeManutencao() {
        // Exigido pelo JPA.
    }

    public EquipeManutencao(String nomeEquipe, String responsavel) {
        this.nomeEquipe = nomeEquipe;
        this.responsavel = responsavel;
    }

    public Long getId() { return id; }
    public String getNomeEquipe() { return nomeEquipe; }
    public String getResponsavel() { return responsavel; }
    public void setNomeEquipe(String nomeEquipe) { this.nomeEquipe = nomeEquipe; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
}
