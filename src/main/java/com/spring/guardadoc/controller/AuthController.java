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
import com.spring.guardadoc.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private AuthService authService;

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
        try {
            Usuario usuario = usuarioRepository.findByEmail(body.email())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (passwordEncoder.matches(body.senha(), usuario.getSenha())) {
                String token = tokenService.generateToken(usuario);

                Set<String> roles = usuario.getRoles().stream()
                        .map(Role::getNome)
                        .collect(Collectors.toSet());

                return ResponseEntity.ok(new ResponseDTO(usuario.getNome(), token, roles, usuario.getId()));
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO("Credenciais inválidas", null, null, null));
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDTO("Usuário não encontrado", null, null, null));
        }
    }




    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody @Valid RegisterRequestDTO body) {
        try {
            ResponseDTO response = authService.register(body);
            return ResponseEntity.ok(response);
        } catch (CustomBadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseDTO(e.getMessage(), null, null, null));
        }
    }
}

