package com.spring.guardadoc.service;

import com.spring.guardadoc.dto.DadosAtualizacaoUsuario;
import com.spring.guardadoc.dto.DadosCadastroUsuario;
import com.spring.guardadoc.exception.InsufficientPermissionException;
import com.spring.guardadoc.exception.UnauthorizedException;
import com.spring.guardadoc.exception.UserNotFoundException;
import com.spring.guardadoc.model.Role;
import com.spring.guardadoc.model.Usuario;
import com.spring.guardadoc.repository.UsuarioRepository;
import com.spring.guardadoc.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    public Usuario criarUsuario(DadosCadastroUsuario dados) {
        Usuario usuario = new Usuario(dados);
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obterUsuario(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario atualizarUsuario(Long id, DadosAtualizacaoUsuario dados, Long usuarioAutenticadoId, String token) {
        if (!id.equals(usuarioAutenticadoId)) {
            throw new UnauthorizedException("Usuário não autorizado a atualizar este perfil.");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Set<String> rolesAutenticadas = tokenService.getRolesFromToken(token);
        if (dados.roles() != null && !dados.roles().isEmpty() &&
                !dados.roles().equals(usuario.getRoles().stream().map(Role::getNome).collect(Collectors.toSet())) &&
                !rolesAutenticadas.contains("ADMIN")) {
            throw new InsufficientPermissionException("Permissão insuficiente para atualizar roles.");
        }

        usuario.atualizarInformacoes(dados);
        return usuarioRepository.save(usuario);
    }

    public boolean deletarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}