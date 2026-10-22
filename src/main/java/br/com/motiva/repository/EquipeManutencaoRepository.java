package br.com.motiva.repository;

import br.com.motiva.model.EquipeManutencao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipeManutencaoRepository extends JpaRepository<EquipeManutencao, Long> {
    List<EquipeManutencao> findByNomeEquipeContainingIgnoreCase(String nomeEquipe);
}
