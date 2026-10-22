package br.com.motiva.controller;

import br.com.motiva.dto.RelatorioResponse;
import br.com.motiva.service.GeradorRelatorioService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioPrioridadeController {
    private final GeradorRelatorioService service;

    public RelatorioPrioridadeController(GeradorRelatorioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RelatorioResponse> gerar() {
        RelatorioResponse response = service.gerarRelatorio();
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<RelatorioResponse> listarHistorico() {
        return service.listarHistorico();
    }

    @GetMapping("/periodo")
    public List<RelatorioResponse> consultarPeriodo(
            @RequestParam @DateTimeFormat(iso = DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DATE_TIME) LocalDateTime fim) {
        return service.consultarPeriodo(inicio, fim);
    }

    @GetMapping("/{id}")
    public RelatorioResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}
