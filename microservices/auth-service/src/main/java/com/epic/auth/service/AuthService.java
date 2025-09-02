package com.epic.auth.service;

import com.epic.auth.config.AuthConfig;
import com.epic.auth.entity.Role;
import com.epic.auth.entity.User;
import com.epic.auth.exception.InvalidCredentialsException;
import com.epic.auth.repository.RoleRepository;
import com.epic.auth.repository.UserRepository;
import com.epic.shared.dto.UserInfoDto;
import com.epic.shared.enums.CommonStatus;
import com.epic.shared.exception.UserInactiveException;
import com.epic.shared.exception.UserNotFoundException;
import com.epic.shared.security.JwtUtil;
import lombok.RequiredArgsConstructor;
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
 *     <li>Validação de dados para o Login e geração do token JWT.</li>
 *     <li>Registo de novos utilizadores com password criptografada.</li>
 *     <li>Alteração da Password do utilizador</li>
 *     <li>Obter dados do utilizador através do seu Id/Email</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthConfig authConfig;

    /**
     * Autentica o utilizador e gera um token JWT.
     *
     * @param email    e-mail do utilizador.
     * @param password password em texto simples.
     * @return token JWT válido.
     * @throws UserInactiveException se o utilizador estiver inativo.
     * @throws RuntimeException se as credenciais forem inválidas.
     */
    public String login(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Email or Password"));

        if(user.getStatus() == CommonStatus.Inactive){
            throw new UserInactiveException("Inactive Account. Please active it before logging in.");
        }

        if(!email.equals(user.getEmail()) || !passwordEncoder.matches(password, user.getPassword())){
            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        UserInfoDto dto = toDto(user);
        return jwtUtil.generateToken(dto);
    }


    /**
     * Regista um novo utilizador no sistema.
     * <p>
     * As passwords são criptografadas antes de serem salvas.
     * O ID da role é fixado inicialmente (pode ser alterado em versões futuras).
     * </p>
     *
     * @param dto objeto {@link UserInfoDto} que contem os dados do novo utilizador.
     * @param rawPassword password não codificada.
     * @return utilizador criado e persistido.
     * @throws RuntimeException se já existir um utilizador com o mesmo e-mail.
     */
    public User register (UserInfoDto dto, String rawPassword){
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new RuntimeException("Email Already Exists");
        }

        User user = new User();
        Role role = roleRepository.findRoleById(authConfig.getUserRoleId());

        user.setRole(role);
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setBirthdate(dto.getBirthDate());
        user.setCountry(dto.getCountry());
        user.setStatus(CommonStatus.Active);

        return userRepository.save(user);
    }


    /**
     * Altera a password de um utilizador.
     *
     * @param userId        ID do utilizador.
     * @param oldPassword   password atual (não codificada).
     * @param newPassword   nova password (não codificada).
     */
    public void changePassword(UUID userId, String oldPassword, String newPassword){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            throw new RuntimeException("Invalid Password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Obtém os dados de um utilizador através do seu ID.
     */
    public UserInfoDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toDto(user);
    }

    /**
     * Obtém os dados de um utilizador através do seu e-mail.
     */
    public UserInfoDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return toDto(user);
    }

    /**
     * Converte um User Entity para UserInfoDto.
     */
    private UserInfoDto toDto(User user) {
        return UserInfoDto.builder()
                .id(user.getId())
                .roleId(user.getRole().getId())
                .roleName(user.getRole().getRoleName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthdate())
                .country(user.getCountry())
                .status(user.getStatus().toString())
                .build();
    }

}
