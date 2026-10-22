package br.com.motiva.service;

import br.com.motiva.dto.EquipeRequest;
import br.com.motiva.dto.EquipeResponse;
import br.com.motiva.exception.ResourceNotFoundException;
import br.com.motiva.model.EquipeManutencao;
import br.com.motiva.repository.EquipeManutencaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipeManutencaoService {
    private final EquipeManutencaoRepository repository;

    public EquipeManutencaoService(EquipeManutencaoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EquipeResponse> listar(String nome) {
        List<EquipeManutencao> equipes = (nome == null || nome.isBlank())
                ? repository.findAll() : repository.findByNomeEquipeContainingIgnoreCase(nome.trim());
        return equipes.stream().map(this::paraResponse).toList();
    }

    @Transactional(readOnly = true)
    public EquipeResponse buscarPorId(Long id) {
        return paraResponse(encontrar(id));
    }

    @Transactional
    public EquipeResponse criar(EquipeRequest request) {
        validar(request);
        return paraResponse(repository.save(new EquipeManutencao(
                request.nomeEquipe().trim(), request.responsavel().trim())));
    }

    @Transactional
    public EquipeResponse atualizar(Long id, EquipeRequest request) {
        validar(request);
        EquipeManutencao equipe = encontrar(id);
        equipe.setNomeEquipe(request.nomeEquipe().trim());
        equipe.setResponsavel(request.responsavel().trim());
        return paraResponse(repository.save(equipe));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(encontrar(id));
    }

    private EquipeManutencao encontrar(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("O ID deve ser positivo.");
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipe não encontrada: id=" + id));
    }

    private void validar(EquipeRequest request) {
        if (request == null || request.nomeEquipe() == null || request.nomeEquipe().isBlank())
            throw new IllegalArgumentException("O nome da equipe é obrigatório.");
        if (request.responsavel() == null || request.responsavel().isBlank())
            throw new IllegalArgumentException("O responsável é obrigatório.");
        if (request.nomeEquipe().trim().length() > 100 || request.responsavel().trim().length() > 100)
            throw new IllegalArgumentException("Nome da equipe e responsável devem ter até 100 caracteres.");
    }

    private EquipeResponse paraResponse(EquipeManutencao equipe) {
        return new EquipeResponse(equipe.getId(), equipe.getNomeEquipe(), equipe.getResponsavel());
    }
}
