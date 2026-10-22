package br.com.motiva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IntervencaoRequest(
        @NotBlank(message = "O tipo da intervenção é obrigatório.")
        @Size(max = 100, message = "O tipo deve ter até 100 caracteres.")
        String tipo,

        @NotBlank(message = "A descrição é obrigatória.")
        @Size(max = 500, message = "A descrição deve ter até 500 caracteres.")
        String descricao
) { }
