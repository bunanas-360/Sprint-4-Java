package br.com.motiva.service;

import br.com.motiva.dto.IntervencaoRequest;
import br.com.motiva.dto.IntervencaoResponse;
import br.com.motiva.exception.ResourceNotFoundException;
import br.com.motiva.model.IntervencaoOperacionalEntity;
import br.com.motiva.repository.IntervencaoOperacionalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntervencaoOperacionalService {
    private final IntervencaoOperacionalRepository repository;

    public IntervencaoOperacionalService(IntervencaoOperacionalRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<IntervencaoResponse> listar(String tipo) {
        List<IntervencaoOperacionalEntity> lista = (tipo == null || tipo.isBlank())
                ? repository.findAll()
                : repository.findByTipoContainingIgnoreCase(tipo.trim());
        return lista.stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public IntervencaoResponse buscarPorId(Long id) {
        return paraResponse(encontrar(id));
    }

    @Transactional
    public IntervencaoResponse criar(IntervencaoRequest request) {
        validar(request);
        return paraResponse(repository.save(new IntervencaoOperacionalEntity(
                request.tipo().trim(), request.descricao().trim())));
    }

    @Transactional
    public IntervencaoResponse atualizar(Long id, IntervencaoRequest request) {
        validar(request);
        IntervencaoOperacionalEntity entity = encontrar(id);
        entity.setTipo(request.tipo().trim());
        entity.setDescricao(request.descricao().trim());
        return paraResponse(repository.save(entity));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(encontrar(id));
    }

    private IntervencaoOperacionalEntity encontrar(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("O ID deve ser positivo.");
        return repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Intervenção não encontrada: id=" + id));
    }

    private void validar(IntervencaoRequest request) {
        if (request == null || request.tipo() == null || request.tipo().isBlank())
            throw new IllegalArgumentException("O tipo da intervenção é obrigatório.");
        if (request.descricao() == null || request.descricao().isBlank())
            throw new IllegalArgumentException("A descrição da intervenção é obrigatória.");
        if (request.tipo().trim().length() > 100 || request.descricao().trim().length() > 500)
            throw new IllegalArgumentException("O tipo deve ter até 100 e a descrição até 500 caracteres.");
    }

    private IntervencaoResponse paraResponse(IntervencaoOperacionalEntity entity) {
        return new IntervencaoResponse(entity.getId(), entity.getTipo(), entity.getDescricao());
    }
}
