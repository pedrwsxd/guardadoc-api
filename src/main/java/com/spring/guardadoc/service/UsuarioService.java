package com.spring.guardadoc.service;

import com.spring.guardadoc.dto.DadosAtualizacaoUsuario;
import com.spring.guardadoc.dto.DadosCadastroUsuario;
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
            throw new RuntimeException("Usuário não autorizado a atualizar este perfil.");
        }

        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verificação de roles
        Set<String> rolesAutenticadas = tokenService.getRolesFromToken(token);
        if (dados.roles() != null && !dados.roles().isEmpty() &&
                !dados.roles().equals(usuario.getRoles().stream().map(Role::getNome).collect(Collectors.toSet())) &&
                !rolesAutenticadas.contains("ROLE_ADMIN")) {
            throw new RuntimeException("Permissão insuficiente para atualizar roles.");
        }

        // Atualizar campos individualmente
        if (dados.nome() != null) {
            usuario.setNome(dados.nome());
        }
        if (dados.email() != null) {
            usuario.setEmail(dados.email());
        }
        if (dados.senha() != null) {
            usuario.setSenha(dados.senha());
        }
        if (dados.roles() != null && !dados.roles().isEmpty()) {
            Set<Role> novasRoles = dados.roles().stream().map(Role::new).collect(Collectors.toSet());
            usuario.setRoles(novasRoles);
        }

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
