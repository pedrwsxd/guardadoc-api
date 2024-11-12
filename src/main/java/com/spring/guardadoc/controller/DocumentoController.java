package com.spring.guardadoc.controller;

import com.spring.guardadoc.dto.DocumentoDTO;
import com.spring.guardadoc.dto.DocumentoMapper;
import com.spring.guardadoc.model.Documento;
import com.spring.guardadoc.service.DocumentoService;
import com.spring.guardadoc.service.QRCodeService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;
    private final QRCodeService qrCodeService;

    public DocumentoController(DocumentoService documentoService, QRCodeService qrCodeService) {
        this.documentoService = documentoService;
        this.qrCodeService = qrCodeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocumento(@RequestParam("file") @NotNull MultipartFile file,
                                             @RequestParam("usuarioId") @NotNull Long usuarioId) {
        try {
            Documento novoDocumento = documentoService.salvarDocumento(file, usuarioId);
            return new ResponseEntity<>(DocumentoMapper.toDTO(novoDocumento), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo.");
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DocumentoDTO>> listarDocumentosPorUsuario(@PathVariable @NotNull Long usuarioId) {
        List<Documento> documentos = documentoService.encontrarPorUsuarioId(usuarioId);
        List<DocumentoDTO> documentoDTOs = documentos.stream()
                .map(DocumentoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(documentoDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obterDocumentoPorId(@PathVariable @NotNull Long id) {
        Optional<Documento> documentoOpt = documentoService.encontrarPorId(id);
        if (documentoOpt.isPresent()) {
            Documento documento = documentoOpt.get();
            DocumentoDTO documentoDTO = DocumentoMapper.toDTO(documento);
            return ResponseEntity.ok(documentoDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento não encontrado.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarDocumento(@PathVariable @NotNull Long id,
                                                @RequestParam("file") MultipartFile file,
                                                @RequestParam("usuarioId") @NotNull Long usuarioId) {
        Optional<Documento> documentoOpt = documentoService.encontrarPorId(id);
        if (documentoOpt.isPresent()) {
            Documento documento = documentoOpt.get();
            String novoCaminhoArquivo = documentoService.salvarDocumento(file, usuarioId).getCaminhoArquivo();

            documento.setNome(file.getOriginalFilename());
            documento.setTipo(file.getContentType());
            documento.setCaminhoArquivo(novoCaminhoArquivo);

            Documento documentoAtualizado = documentoService.atualizarDocumento(documento);
            return ResponseEntity.ok(DocumentoMapper.toDTO(documentoAtualizado));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento não encontrado.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDocumento(@PathVariable @NotNull Long id) {
        Optional<Documento> documentoOpt = documentoService.encontrarPorId(id);
        if (documentoOpt.isPresent()) {
            documentoService.deletarDocumento(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }



}