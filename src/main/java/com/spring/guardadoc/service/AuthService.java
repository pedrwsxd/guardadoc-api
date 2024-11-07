package com.spring.guardadoc.service;

import com.spring.guardadoc.dto.RegisterRequestDTO;
import com.spring.guardadoc.dto.ResponseDTO;
import com.spring.guardadoc.exception.CustomBadRequestException;
import com.spring.guardadoc.model.Role;
import com.spring.guardadoc.model.Usuario;
import com.spring.guardadoc.repository.RoleRepository;
import com.spring.guardadoc.repository.UsuarioRepository;
import com.spring.guardadoc.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public ResponseDTO register(RegisterRequestDTO body) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(body.email());

        if (usuarioOptional.isEmpty()) {
            Usuario newUsuario = new Usuario();
            newUsuario.setSenha(passwordEncoder.encode(body.senha()));
            newUsuario.setEmail(body.email());
            newUsuario.setNome(body.nome());
            newUsuario.setTelefone(body.telefone());

            Role clienteRole = roleRepository.findByNome("ROLE_CLIENTE")
                    .orElseThrow(() -> new CustomBadRequestException("Default role CLIENTE not found"));
            newUsuario.setRoles(Set.of(clienteRole));

            usuarioRepository.save(newUsuario);

            String token = tokenService.generateToken(newUsuario);

            Set<String> roles = newUsuario.getRoles().stream()
                    .map(Role::getNome)
                    .collect(Collectors.toSet());

            return new ResponseDTO(newUsuario.getNome(), token, roles, newUsuario.getId());
        }

        throw new CustomBadRequestException("Email already in use");
    }
}
