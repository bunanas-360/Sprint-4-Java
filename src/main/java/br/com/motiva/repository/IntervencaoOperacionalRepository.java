package br.com.motiva.repository;

import br.com.motiva.model.IntervencaoOperacionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntervencaoOperacionalRepository extends JpaRepository<IntervencaoOperacionalEntity, Long> {
    List<IntervencaoOperacionalEntity> findByTipoIgnoreCase(String tipo);
    List<IntervencaoOperacionalEntity> findByTipoContainingIgnoreCase(String tipo);
}
