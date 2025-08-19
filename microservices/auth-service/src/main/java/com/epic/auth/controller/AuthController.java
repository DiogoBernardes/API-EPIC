package com.epic.auth.controller;

import com.epic.auth.dto.*;
import com.epic.auth.entity.User;
import com.epic.auth.service.AuthService;
import com.epic.shared.dto.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST responsável por gerir os endpoints de autenticação e registo dos utilizadores.
 * <p>
 * Todos os endpoints estão mapeados sob o caminho base <code>/auth</code>.
 * </p>
 *
 * <ul>
 *     <li><b>/login</b>: autenticação e emissão de token JWT.</li>
 *     <li><b>/register</b>: Registo do novo utilizador.</li>
 *     <li><b>/logout</b>: invalidação do token JWT atual.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Autenticação e registo de utilizadores")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    /**
     * Endpoint para autenticação do utilizador.
     * <p>
     * Recebe email e password, valida as credenciais e retorna um token JWT caso sejam válidas.
     * </p>
     *
     * @param request objeto {@link LoginRequestDto} que contém o email e a password do utilizador.
     * @return {@link ResponseEntity} contem o token JWT gerado.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request.getEmail(), request.getPassword()));
    }

    /**
     * Endpoint para registo de um novo utilizador.
     * <p>
     * Recebe os dados do utilizador e senha, cria o utilizador e retorna a entidade criada.
     * </p>
     *
     * @param request objeto {@link RegisterRequestDto} que contem as informações do utilizador a ser registado.
     * @return {@link ResponseEntity} contem o {@link User} criado.
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequestDto request) {
        UserDto dto = UserDto.builder()
                .roleId(request.getRoleId())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthdate())
                .country(request.getCountry())
                .build();

        return ResponseEntity.ok(authService.register(dto, request.getPassword()));
    }
}
