package br.com.motiva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record TrechoRequest(
        @NotNull(message = "O KM é obrigatório.")
        @PositiveOrZero(message = "O KM deve ser maior ou igual a zero.")
        Integer km,

        @NotNull(message = "O crescimento da vegetação é obrigatório.")
        @PositiveOrZero(message = "O crescimento da vegetação não pode ser negativo.")
        Double crescimentoVegetacao,

        @NotBlank(message = "O tipo do trecho é obrigatório.")
        @Size(max = 50, message = "O tipo do trecho deve ter até 50 caracteres.")
        String tipoTrecho
) { }
