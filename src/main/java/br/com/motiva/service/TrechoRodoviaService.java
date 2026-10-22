package br.com.motiva.service;

import br.com.motiva.dto.TrechoRequest;
import br.com.motiva.dto.TrechoResponse;
import br.com.motiva.exception.ResourceNotFoundException;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.repository.TrechoRodoviaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.util.List;

@Service
public class TrechoRodoviaService {
    private final TrechoRodoviaRepository repository;

    public TrechoRodoviaService(TrechoRodoviaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TrechoResponse> listar(String tipo) {
        List<TrechoRodovia> trechos = (tipo == null || tipo.isBlank())
                ? repository.findAllByOrderByKmAsc()
                : repository.findByTipoTrechoIgnoreCase(tipo.trim());
        return trechos.stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TrechoResponse> buscarPorVegetacaoMinima(Double minimo) {
        if (minimo == null || !Double.isFinite(minimo) || minimo < 0) {
            throw new IllegalArgumentException("O parâmetro minimo deve ser um número maior ou igual a zero.");
        }
        return repository.findByCrescimentoVegetacaoGreaterThanEqual(BigDecimal.valueOf(minimo))
                .stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public TrechoResponse buscarPorId(Long id) {
        return paraResponse(encontrar(id));
    }

    @Transactional
    public TrechoResponse criar(TrechoRequest request) {
        validar(request);
        TrechoRodovia trecho = new TrechoRodovia(
                request.km(), request.crescimentoVegetacao(), request.tipoTrecho().trim());
        return paraResponse(repository.save(trecho));
    }

    @Transactional
    public TrechoResponse atualizar(Long id, TrechoRequest request) {
        validar(request);
        TrechoRodovia trecho = encontrar(id);
        trecho.setKm(request.km());
        trecho.setCrescimentoVegetacao(request.crescimentoVegetacao());
        trecho.setTipoTrecho(request.tipoTrecho().trim());
        return paraResponse(repository.save(trecho));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(encontrar(id));
    }

    private TrechoRodovia encontrar(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("O ID deve ser um número positivo.");
        }
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trecho não encontrado: id=" + id));
    }

    private void validar(TrechoRequest request) {
        if (request == null) throw new IllegalArgumentException("O corpo da requisição é obrigatório.");
        if (request.km() == null || request.km() < 0)
            throw new IllegalArgumentException("O KM deve ser maior ou igual a zero.");
        if (request.crescimentoVegetacao() == null || !Double.isFinite(request.crescimentoVegetacao())
                || request.crescimentoVegetacao() < 0)
            throw new IllegalArgumentException("O crescimento da vegetação deve ser um número maior ou igual a zero.");
        if (request.tipoTrecho() == null || request.tipoTrecho().isBlank())
            throw new IllegalArgumentException("O tipo do trecho é obrigatório.");
        if (request.tipoTrecho().trim().length() > 50)
            throw new IllegalArgumentException("O tipo do trecho deve ter até 50 caracteres.");
    }

    private TrechoResponse paraResponse(TrechoRodovia trecho) {
        return new TrechoResponse(trecho.getId(), trecho.getKm(),
                trecho.getCrescimentoVegetacao().doubleValue(), trecho.getTipoTrecho());
    }
}
