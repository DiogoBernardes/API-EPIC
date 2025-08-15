package com.epic.auth.service;

import com.epic.auth.entity.User;
import com.epic.auth.repository.UserRepository;
import com.epic.shared.exception.UserInactiveException;
import com.epic.shared.security.JwtUtil;
import com.epic.shared.dto.UserDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Serviço responsável pelas operações de autenticação e gestão de utilizadores.
 * <p>
 * Este serviço oferece métodos para login, registo e logout, integrando
 * autenticação via JWT e codificação de senha.
 * </p>
 *
 * <ul>
 *     <li>Validação de dados e geração de token JWT.</li>
 *     <li>Registo de novos utilizadores com password criptografada.</li>
 *     <li>Logout (invalidação de token, se aplicável).</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Realiza o login de um utilizador autenticando os seus dados e gerando um token JWT.
     *
     * @param email e-mail do utilizador.
     * @param password password não codificada.
     * @return token JWT válido para autenticação de requisições subsequentes.
     * @throws RuntimeException se o e-mail não existir, a password estiver incorreta ou a conta estiver inativa.
     * @throws UserInactiveException se o utilizador estiver com status "Inactive".
     */
    public String login(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid Email or Password"));

        if("Inactive".equals(user.getStatus())){
            throw new UserInactiveException("Inactive Account. Please active it before logging in.");
        }

        if(!email.equals(user.getEmail()) || !passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid Email or Password");
        }

        // Create DTO for token
        UserDto dto = UserDto.builder()
                .id(user.getId())
                .roleId(user.getRoleId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus())
                .build();

        return jwtUtil.generateToken(dto);
    }


    /**
     * Regista um novo utilizador no sistema.
     * <p>
     * As passwords são criptografadas antes de serem salvas.
     * O ID da role é fixado inicialmente (pode ser alterado em versões futuras).
     * </p>
     *
     * @param dto objeto {@link UserDto} que contem os dados do novo utilizador.
     * @param rawPassword password não codificada.
     * @return utilizador criado e persistido.
     * @throws RuntimeException se já existir um utilizador com o mesmo e-mail.
     */
    public User register (UserDto dto, String rawPassword){
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new RuntimeException("Email Already Exists");
        }

        User user = new User();
        user.setRoleId(UUID.fromString("433ed5bb-2766-423d-b445-3ad17a3411a7"));
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setBirthdate(dto.getBirthDate());
        user.setCountry(dto.getCountry());
        user.setStatus("Active");

        return userRepository.save(user);
    }
}
