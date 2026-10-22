package br.com.motiva.dto;

import java.time.LocalDateTime;

public record RelatorioResponse(
        Long id,
        LocalDateTime dataGeracao,
        Integer qtUrgente,
        Integer qtCritico,
        Integer qtAtencao,
        Integer qtNormal,
        String resumo
) { }
