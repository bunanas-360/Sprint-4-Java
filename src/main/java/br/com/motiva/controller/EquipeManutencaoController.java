package br.com.motiva.controller;

import br.com.motiva.dto.EquipeRequest;
import br.com.motiva.dto.EquipeResponse;
import br.com.motiva.service.EquipeManutencaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipes")
public class EquipeManutencaoController {
    private final EquipeManutencaoService service;

    public EquipeManutencaoController(EquipeManutencaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<EquipeResponse> listar(@RequestParam(required = false) String nome) {
        return service.listar(nome);
    }

    @GetMapping("/{id}")
    public EquipeResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<EquipeResponse> criar(@Valid @RequestBody EquipeRequest request) {
        EquipeResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public EquipeResponse atualizar(@PathVariable Long id, @Valid @RequestBody EquipeRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
