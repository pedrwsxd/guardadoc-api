package com.spring.guardadoc.dto;

import com.spring.guardadoc.model.Documento;

public class DocumentoMapper {
    public static DocumentoDTO toDTO(Documento documento) {
        return new DocumentoDTO(documento.getId(), documento.getNome(), documento.getCaminhoArquivo());
    }
}