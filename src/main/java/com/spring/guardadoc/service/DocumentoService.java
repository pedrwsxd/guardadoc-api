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

    /**
     * Salva um documento associado a um usuário específico.
     * @param documento O documento a ser salvo.
     * @param usuarioId O ID do usuário ao qual o documento pertence.
     * @return O documento salvo.
     */
    @Transactional
    public Documento salvarDocumento(Documento documento, Long usuarioId) {
        Optional<Usuario> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isPresent()) {
            documento.setUsuario(usuario.get());
            return documentoRepository.save(documento);
        } else {
            throw new IllegalArgumentException("Usuário não encontrado com ID: " + usuarioId);
        }
    }

    /**
     * Encontra um documento pelo seu ID.
     * @param id O ID do documento.
     * @return O documento encontrado, se existir.
     */
    public Optional<Documento> encontrarPorId(Long id) {
        return documentoRepository.findById(id);
    }

    /**
     * Encontra todos os documentos associados a um usuário específico.
     * @param usuarioId O ID do usuário.
     * @return A lista de documentos do usuário.
     */
    @Transactional(readOnly = true)
    public List<Documento> encontrarPorUsuarioId(Long usuarioId) {
        return documentoRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Deleta um documento pelo seu ID.
     * @param id O ID do documento.
     */
    @Transactional
    public void deletarDocumento(Long id) {
        documentoRepository.deleteById(id);
    }

    /**
     * Encontra um usuário pelo seu ID.
     * @param id O ID do usuário.
     * @return O usuário encontrado, se existir.
     */
    public Optional<Usuario> encontrarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }
}