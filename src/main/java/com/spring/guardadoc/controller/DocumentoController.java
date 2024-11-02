package com.spring.guardadoc.controller;

import com.spring.guardadoc.dto.DocumentoDTO;
import com.spring.guardadoc.model.Documento;
import com.spring.guardadoc.model.Usuario;
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
    public ResponseEntity<?> uploadDocumento(@RequestParam("file") @NotNull MultipartFile file, @RequestParam("usuarioId") @NotNull String usuarioId) {
        try {
            Long userId = Long.parseLong(usuarioId);
            Documento documento = new Documento();
            documento.setNome(file.getOriginalFilename());
            documento.setTipo(file.getContentType());
            documento.setDados(file.getBytes());
            Optional<Usuario> usuarioOpt = documentoService.encontrarUsuarioPorId(userId);
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                documento.setUsuario(usuario);
                Documento novoDocumento = documentoService.salvarDocumento(documento);
                return new ResponseEntity<>(novoDocumento, HttpStatus.CREATED);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
            }
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
    public ResponseEntity<?> atualizarDocumento(@PathVariable @NotNull String
                                                        id, @RequestParam("file") MultipartFile file,
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

    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> generateQRCode(@PathVariable String id) {
        try {
            Long docId = Long.parseLong(id);
            Optional<Documento> documento = documentoService.encontrarPorId(docId);
            if (documento.isPresent()) {
                String baseUrl = "http://localhost:8080/api/documentos/";
                String qrContent = baseUrl + id;
                BufferedImage qrCodeImage = qrCodeService.generateQRCodeImage(qrContent);
                ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
                ImageIO.write(qrCodeImage, "PNG", pngOutputStream);
                byte[] qrCodeBytes = pngOutputStream.toByteArray();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                return new ResponseEntity<>(qrCodeBytes, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
