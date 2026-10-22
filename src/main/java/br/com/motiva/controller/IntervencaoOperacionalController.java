package br.com.motiva.controller;

import br.com.motiva.dto.IntervencaoRequest;
import br.com.motiva.dto.IntervencaoResponse;
import br.com.motiva.service.IntervencaoOperacionalService;
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
@RequestMapping("/api/intervencoes")
public class IntervencaoOperacionalController {
    private final IntervencaoOperacionalService service;

    public IntervencaoOperacionalController(IntervencaoOperacionalService service) {
        this.service = service;
    }

    @GetMapping
    public List<IntervencaoResponse> listar(@RequestParam(required = false) String tipo) {
        return service.listar(tipo);
    }

    @GetMapping("/{id}")
    public IntervencaoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<IntervencaoResponse> criar(@Valid @RequestBody IntervencaoRequest request) {
        IntervencaoResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public IntervencaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody IntervencaoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
