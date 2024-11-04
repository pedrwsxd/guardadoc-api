package com.spring.guardadoc.service;

import com.spring.guardadoc.model.Documento;
import com.spring.guardadoc.model.Usuario;
import com.spring.guardadoc.repository.DocumentoRepository;
import com.spring.guardadoc.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public DocumentoService(DocumentoRepository documentoRepository, UsuarioRepository usuarioRepository) {
        this.documentoRepository = documentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Salva um documento no repositório
    public Documento salvarDocumento(Documento documento) {
        return documentoRepository.save(documento);
    }

    // Encontra um documento pelo seu ID
    public Optional<Documento> encontrarPorId(Long id) {
        return documentoRepository.findById(id);
    }

    // Encontra todos os documentos associados a um usuário específico
    @Transactional(readOnly = true)
    public List<Documento> encontrarPorUsuarioId(Long usuarioId) {
        return documentoRepository.findByUsuarioId(usuarioId);
    }

    // Deleta um documento pelo seu ID
    public void deletarDocumento(Long id) {
        documentoRepository.deleteById(id);
    }

    // Encontra um usuário pelo seu ID
    public Optional<Usuario> encontrarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }
}
