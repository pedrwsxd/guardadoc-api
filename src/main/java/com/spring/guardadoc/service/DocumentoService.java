package com.spring.guardadoc.service;

import com.spring.guardadoc.model.Documento;
import com.spring.guardadoc.model.Usuario;
import com.spring.guardadoc.repository.DocumentoRepository;
import com.spring.guardadoc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final S3Service s3Service;

    @Autowired
    public DocumentoService(DocumentoRepository documentoRepository, UsuarioRepository usuarioRepository, S3Service s3Service) {
        this.documentoRepository = documentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.s3Service = s3Service;
    }

    @Transactional
    public Documento salvarDocumento(MultipartFile file, Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado com ID: " + usuarioId);
        }

        try {
            String caminhoArquivo = s3Service.uploadFile(file.getOriginalFilename(), file.getBytes());

            Documento documento = new Documento();
            documento.setNome(file.getOriginalFilename());
            documento.setTipo(file.getContentType());
            documento.setUsuario(usuario.get());
            documento.setCaminhoArquivo(caminhoArquivo);

            return documentoRepository.save(documento);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar o documento no S3", e);
        }
    }

    @Transactional
    public Documento atualizarDocumento(Documento documento) {
        return documentoRepository.save(documento);
    }


    public Optional<Documento> encontrarPorId(Long id) {
        return documentoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Documento> encontrarPorUsuarioId(Long usuarioId) {
        return documentoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public void deletarDocumento(Long id) {
        documentoRepository.deleteById(id);
    }

    public Optional<Usuario> encontrarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }
}