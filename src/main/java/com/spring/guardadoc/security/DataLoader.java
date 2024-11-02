package com.spring.guardadoc.security;

import com.spring.guardadoc.model.Role;
import com.spring.guardadoc.model.Usuario;
import com.spring.guardadoc.repository.RoleRepository;
import com.spring.guardadoc.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostConstruct
    public void loadRoles() {

        Optional<Role> adminRole = roleRepository.findByNome("ROLE_ADMIN");
        if (adminRole.isEmpty()) {

            Role roleAdmin = new Role("ROLE_ADMIN");
            roleRepository.save(roleAdmin);
        }

        Optional<Role> clienteRole = roleRepository.findByNome("ROLE_CLIENTE");
        if (clienteRole.isEmpty()) {

            Role roleCliente = new Role("ROLE_CLIENTE");
            roleRepository.save(roleCliente);
        }
    }

    @Bean
    CommandLineRunner createAdminIfNotExists() {
        return args -> {

            Optional<Role> roleAdmin = roleRepository.findByNome("ROLE_ADMIN");
            if (roleAdmin.isEmpty()) {
                Role adminRole = new Role("ROLE_ADMIN");
                roleRepository.save(adminRole);
            }

            // Verifica se existe algum usuário com o papel ROLE_ADMIN
            Optional<Usuario> existingAdmin = usuarioRepository.findAll().stream()
                    .filter(user -> user.getRoles().stream()
                            .anyMatch(role -> role.getNome().equals("ROLE_ADMIN")))
                    .findFirst();

            if (existingAdmin.isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNome("Admin");
                admin.setEmail("admin@admin.com");
                admin.setSenha(passwordEncoder.encode("admin123")); // Define a senha padrão para admin

                Set<Role> roles = new HashSet<>();
                roles.add(roleRepository.findByNome("ROLE_ADMIN").get());
                admin.setRoles(roles);

                usuarioRepository.save(admin);
                System.out.println("Admin user created: admin@admin.com");
            } else {
                System.out.println("Admin user with ROLE_ADMIN already exists");
            }
        };
    }
}
