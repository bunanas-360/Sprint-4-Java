package br.com.motiva.controller;

import br.com.motiva.dto.TrechoRequest;
import br.com.motiva.dto.TrechoResponse;
import br.com.motiva.service.TrechoRodoviaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/api/trechos")
@Validated
public class TrechoRodoviaController {
    private final TrechoRodoviaService service;

    public TrechoRodoviaController(TrechoRodoviaService service) {
        this.service = service;
    }

    @GetMapping
    public List<TrechoResponse> listar(@RequestParam(required = false) String tipo) {
        return service.listar(tipo);
    }

    @GetMapping("/vegetacao")
    public List<TrechoResponse> buscarPorVegetacao(
            @RequestParam @PositiveOrZero(message = "minimo deve ser maior ou igual a zero") Double minimo) {
        return service.buscarPorVegetacaoMinima(minimo);
    }

    @GetMapping("/{id}")
    public TrechoResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<TrechoResponse> criar(@Valid @RequestBody TrechoRequest request) {
        TrechoResponse response = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public TrechoResponse atualizar(@PathVariable Long id, @Valid @RequestBody TrechoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
