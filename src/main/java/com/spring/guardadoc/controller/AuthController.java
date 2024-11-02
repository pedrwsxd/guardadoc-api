package com.spring.guardadoc.controller;

import com.spring.guardadoc.dto.LoginRequestDTO;
import com.spring.guardadoc.dto.RegisterRequestDTO;
import com.spring.guardadoc.dto.ResponseDTO;
import com.spring.guardadoc.exception.CustomBadRequestException;
import com.spring.guardadoc.exception.UserNotFoundException;
import com.spring.guardadoc.model.Role;
import com.spring.guardadoc.model.Usuario;
import com.spring.guardadoc.repository.RoleRepository;
import com.spring.guardadoc.repository.UsuarioRepository;
import com.spring.guardadoc.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody @Valid LoginRequestDTO body) {
        Usuario usuario = usuarioRepository.findByEmail(body.email())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (passwordEncoder.matches(body.senha(), usuario.getSenha())) {
            String token = tokenService.generateToken(usuario);

            Set<String> roles = usuario.getRoles().stream()
                    .map(Role::getNome)
                    .collect(Collectors.toSet());

            return ResponseEntity.ok(new ResponseDTO(usuario.getNome(), token, roles, usuario.getId()));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody @Valid RegisterRequestDTO body) {
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

            return ResponseEntity.ok(new ResponseDTO(newUsuario.getNome(), token, roles, newUsuario.getId()));
        }

        return ResponseEntity.badRequest().build();
    }
}
