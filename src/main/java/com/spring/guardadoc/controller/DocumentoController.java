package com.spring.guardadoc.controller;

import com.spring.guardadoc.dto.DocumentoDTO;
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

    // Endpoint para upload de documento
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocumento(@RequestParam("file") @NotNull MultipartFile file,
                                             @RequestParam("usuarioId") @NotNull Long usuarioId) {
        try {
            Documento documento = new Documento();
            documento.setNome(file.getOriginalFilename());
            documento.setTipo(file.getContentType());
            documento.setDados(file.getBytes());

            Documento novoDocumento = documentoService.salvarDocumento(documento, usuarioId);
            return new ResponseEntity<>(new DocumentoDTO(novoDocumento.getId(), novoDocumento.getNome()), HttpStatus.CREATED);
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
    public ResponseEntity<?> obterDocumentoPorId(@PathVariable @NotNull Long id) {
        Optional<Documento> documentoOpt = documentoService.encontrarPorId(id);
        if (documentoOpt.isPresent()) {
            Documento documento = documentoOpt.get();
            DocumentoDTO documentoDTO = new DocumentoDTO(documento.getId(), documento.getNome());
            return ResponseEntity.ok(documentoDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento não encontrado.");
        }
    }

    // Endpoint para atualizar documento
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarDocumento(@PathVariable @NotNull Long id,
                                                @RequestParam("file") MultipartFile file,
                                                @RequestParam("usuarioId") @NotNull Long usuarioId) {
        try {
            Optional<Documento> documentoOpt = documentoService.encontrarPorId(id);
            if (documentoOpt.isPresent()) {
                Documento documento = documentoOpt.get();
                documento.setNome(file.getOriginalFilename());
                documento.setTipo(file.getContentType());
                documento.setDados(file.getBytes());

                Documento documentoAtualizado = documentoService.salvarDocumento(documento, usuarioId);
                return ResponseEntity.ok(new DocumentoDTO(documentoAtualizado.getId(), documentoAtualizado.getNome()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Documento não encontrado.");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar o arquivo.");
        }
    }

    // Endpoint para deletar documento pelo ID
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

    // Endpoint para gerar QR Code para um documento específico
    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable @NotNull Long id) {
        Optional<Documento> documentoOpt = documentoService.encontrarPorId(id);
        if (documentoOpt.isPresent()) {
            try {
                String baseUrl = "http://localhost:8080/api/documentos/";
                String qrContent = baseUrl + id;
                BufferedImage qrCodeImage = qrCodeService.generateQRCodeImage(qrContent);
                ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
                ImageIO.write(qrCodeImage, "PNG", pngOutputStream);
                byte[] qrCodeBytes = pngOutputStream.toByteArray();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                return new ResponseEntity<>(qrCodeBytes, headers, HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}