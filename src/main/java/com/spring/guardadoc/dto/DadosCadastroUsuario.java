package com.spring.guardadoc.dto;


import com.spring.guardadoc.model.Role;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record DadosCadastroUsuario(

		@NotBlank
		String nome,

		@NotBlank
		String telefone,

		@NotBlank
		String email,

		@NotBlank
		String senha,

		@NotBlank
		Set<Role> roles

) {

}
