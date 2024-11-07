package com.spring.guardadoc.controller;

import com.spring.guardadoc.model.Documento;
import com.spring.guardadoc.repository.DocumentoRepository;
import com.spring.guardadoc.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
public class FileDownloadController extends ResponseEntityExceptionHandler {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, @RequestParam Long usuarioId) {
        try {
            Optional<Documento> documentoOptional = documentoRepository.findById(id);
            if (!documentoOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            Documento documento = documentoOptional.get();
            if (!documento.getUsuario().getId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            Path filePath = Paths.get(documento.getCaminhoArquivo());
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro interno do servidor: " + e.getMessage());
    }
}