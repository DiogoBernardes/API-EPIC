package com.epic.auth.controller;

import com.epic.auth.dto.*;
import com.epic.auth.entity.User;
import com.epic.auth.service.AuthService;
import com.epic.shared.dto.UserInfoDto;
import com.epic.shared.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/**
 * Controlador REST responsável pelos endpoints de autenticação, registo e gestão de perfil de utilizadores.
 * <p>
 * Todos os endpoints estão sob o caminho base <code>/auth</code> e usam JWT para autenticação.
 * </p>
 * <p>
 * Funcionalidades principais:
 * <ul>
 *     <li>Autenticação de utilizadores e emissão de tokens JWT (/login).</li>
 *     <li>Registo de novos utilizadores (/register).</li>
 *     <li>Alteração de password de utilizadores autenticados (/user/changePassword).</li>
 *     <li>Consulta de utilizador por ID ou email (/user/id/{id}, /user/email/{email}).</li>
 * </ul>
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Autenticação, registo e perfil dos utilizadores")
@SecurityRequirement(name = "bearerAuth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }


    /**
     * Endpoint para autenticação de utilizador.
     *
     * @param request objeto {@link LoginRequestDto} com email e password.
     * @return {@link ResponseEntity} com token JWT gerado.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request.getEmail(), request.getPassword()));
    }

    /**
     * Endpoint para registo de um novo utilizador.
     *
     * @param request objeto {@link RegisterRequestDto} com dados do utilizador.
     * @return {@link ResponseEntity} com o {@link User} criado.
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequestDto request) {
        UserInfoDto dto = UserInfoDto.builder()
                .roleId(request.getRoleId())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .birthDate(request.getBirthdate())
                .country(request.getCountry())
                .build();

        return ResponseEntity.ok(authService.register(dto, request.getPassword()));
    }

    /**
     * Endpoint para alteração de password de um utilizador autenticado.
     *
     * @param authorizationHeader cabeçalho Authorization com o token JWT.
     * @param oldPassword password atual.
     * @param newPassword nova password.
     * @return {@link ResponseEntity} com mensagem de sucesso.
     */
    @PutMapping("/user/changePassword")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("User not authenticated!");
        }

        String token = authorizationHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token invalid or expired");
        }

        Claims claims = jwtUtil.extractClaims(token);
        UUID userId = UUID.fromString(claims.get("id", String.class));

        authService.changePassword(userId, oldPassword, newPassword);

        return ResponseEntity.ok("Password updated with success!!");
    }

    /**
     * Endpoint para obter utilizador através do ID.
     *
     * @param id UUID do utilizador.
     * @return {@link ResponseEntity} com {@link UserInfoDto}.
     */
    @GetMapping("/user/id/{id}")
    public ResponseEntity<UserInfoDto> getUserById( @PathVariable("id") UUID id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    /**
     * Endpoint para obter utilizador através do email.
     *
     * @param email e-mail do utilizador.
     * @return {@link ResponseEntity} com {@link UserInfoDto}.
     */
    @GetMapping("/user/email/{email}")
    public ResponseEntity<UserInfoDto> getUserByEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(authService.getUserByEmail(email));
    }
}
