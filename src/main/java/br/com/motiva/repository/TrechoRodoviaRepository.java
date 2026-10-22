package br.com.motiva.repository;

import br.com.motiva.model.TrechoRodovia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;

import java.util.List;

public interface TrechoRodoviaRepository extends JpaRepository<TrechoRodovia, Long> {
    List<TrechoRodovia> findAllByOrderByKmAsc();
    List<TrechoRodovia> findByCrescimentoVegetacaoGreaterThanEqual(BigDecimal minimo);
    List<TrechoRodovia> findByTipoTrechoIgnoreCase(String tipoTrecho);
}
