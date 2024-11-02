package com.spring.guardadoc.controller;

import com.spring.guardadoc.dto.DocumentoDTO;
import com.spring.guardadoc.model.Documento;
import com.spring.guardadoc.model.Usuario;
import com.spring.guardadoc.service.DocumentoService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    // Endpoint para upload de documento
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocumento(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam("usuarioId") @NotNull String usuarioId) { // Alterado para String
        try {
            Long userId = Long.parseLong(usuarioId); // Conversão para Long
            Documento documento = new Documento();
            documento.setNome(file.getOriginalFilename());
            documento.setTipo(file.getContentType());
            documento.setDados(file.getBytes());
            documento.setUsuario(new Usuario(userId));

            Documento novoDocumento = documentoService.salvarDocumento(documento);
            return new ResponseEntity<>(novoDocumento, HttpStatus.CREATED);

        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID do usuário deve ser um número.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo.");
        }
    }

    // Endpoint para listar documentos por usuário
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DocumentoDTO>> listarDocumentosPorUsuario(@PathVariable @NotNull Long usuarioId) {
        List<Documento> documentos = documentoService.encontrarPorUsuarioId(usuarioId);
        List<DocumentoDTO> documentoDTOs = documentos.stream()
                .map(doc -> new DocumentoDTO(doc.getId(), doc.getNome()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(documentoDTOs);
    }

    // Endpoint para obter documento pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obterDocumentoPorId(@PathVariable @NotNull String id) { // Alterado para String
        try {
            Long docId = Long.parseLong(id); // Conversão para Long
            Optional<Documento> documento = documentoService.encontrarPorId(docId);
            return documento.map(doc -> ResponseEntity.ok(new DocumentoDTO(doc.getId(), doc.getNome())))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID do documento deve ser um número.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarDocumento(@PathVariable @NotNull String id, @RequestParam("file") MultipartFile file,
                                                @RequestParam("usuarioId") @NotNull String usuarioId) {
        try {
            Long docId = Long.parseLong(id);
            Long userId = Long.parseLong(usuarioId);
            Optional<Documento> documentoExistente = documentoService.encontrarPorId(docId);
            if (documentoExistente.isPresent()) {
                Documento documento = documentoExistente.get();
                documento.setNome(file.getOriginalFilename());
                documento.setTipo(file.getContentType());
                documento.setDados(file.getBytes());
                documento.setUsuario(new Usuario(userId));
                Documento documentoAtualizado = documentoService.salvarDocumento(documento);
                return ResponseEntity.ok(documentoAtualizado);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento não encontrado.");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID deve ser um número.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo.");
        }
    }

    // Endpoint para deletar documento pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDocumento(@PathVariable @NotNull String id) { // Alterado para String
        try {
            Long docId = Long.parseLong(id); // Conversão para Long
            documentoService.deletarDocumento(docId);
            return ResponseEntity.noContent().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
