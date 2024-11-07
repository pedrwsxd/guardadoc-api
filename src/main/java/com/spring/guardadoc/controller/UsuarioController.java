package com.spring.guardadoc.controller;

import com.spring.guardadoc.dto.DadosAtualizacaoUsuario;
import com.spring.guardadoc.dto.DadosCadastroUsuario;
import com.spring.guardadoc.exception.InsufficientPermissionException;
import com.spring.guardadoc.exception.UnauthorizedException;
import com.spring.guardadoc.exception.UserNotFoundException;
import com.spring.guardadoc.model.Usuario;
import com.spring.guardadoc.security.TokenService;
import com.spring.guardadoc.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TokenService tokenService;

    @GetMapping
    public ResponseEntity<String> getUser() {
        return ResponseEntity.ok("sucesso!");
    }

    @PostMapping("/add")
    public ResponseEntity<Usuario> criarUsuario(@RequestBody DadosCadastroUsuario dados) {
        Usuario usuario = usuarioService.criarUsuario(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/listar/{id}")
    public ResponseEntity<Usuario> obterUsuario(@PathVariable Long id) {
        return usuarioService.obterUsuario(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody DadosAtualizacaoUsuario dados, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            Long usuarioAutenticadoId = tokenService.getIdFromToken(token);
            Usuario usuario = usuarioService.atualizarUsuario(id, dados, usuarioAutenticadoId, token);
            return ResponseEntity.ok(usuario);
        } catch (UnauthorizedException | InsufficientPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        if (usuarioService.deletarUsuario(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
