package com.spring.guardadoc.dto;

public record DocumentoDTO(
        Long id,
        String nome,
        String caminhoArquivo
) {
}