package com.spring.guardadoc.model;

import com.spring.guardadoc.dto.DadosAtualizacaoUsuario;
import com.spring.guardadoc.dto.DadosCadastroUsuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String telefone;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Documento> documentos;


    public Usuario(Long id) {
        this.id = id;
    }


    public void atualizarInformacoes(@Valid DadosAtualizacaoUsuario dados) {
        if (dados.nome() != null) {
            this.setNome(dados.nome());
        }
        if (dados.telefone() != null) {
            this.setTelefone(dados.telefone());
        }
        if (dados.email() != null) {
            this.setEmail(dados.email());
        }
        if (dados.senha() != null) {
            this.setSenha(dados.senha());
        }
        if (dados.roles() != null && !dados.roles().isEmpty()) {
            this.setRoles(dados.roles());
        }
    }
    public Usuario(DadosCadastroUsuario dados) {
    this.nome = dados.nome();
    this.email = dados.email();
    this.senha = dados.senha();
    this.telefone = dados.telefone();
    // Inicialize outros campos conforme necessário
}
}
