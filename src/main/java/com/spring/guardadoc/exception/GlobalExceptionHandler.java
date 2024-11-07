package com.spring.guardadoc.exception;

import com.spring.guardadoc.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler extends RuntimeException {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleUserNotFoundException(UserNotFoundException ex) {
        // Retorna uma resposta 400 com uma mensagem clara para "usuário não encontrado"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseDTO("Usuário não encontrado", null, null, null));
    }

}
