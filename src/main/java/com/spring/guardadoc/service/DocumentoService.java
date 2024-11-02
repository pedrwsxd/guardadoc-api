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
    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Documento salvarDocumento(Documento documento) {
        return documentoRepository.save(documento);
    }

    public Optional<Documento> encontrarPorId(Long id) {
        return documentoRepository.findById(id);
    }

    @Transactional
    public List<Documento> encontrarPorUsuarioId(Long usuarioId) {
        return documentoRepository.findByUsuarioId(usuarioId);
    }

    public void deletarDocumento(Long id) {
        documentoRepository.deleteById(id);
    }

    public Optional<Usuario> encontrarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

}