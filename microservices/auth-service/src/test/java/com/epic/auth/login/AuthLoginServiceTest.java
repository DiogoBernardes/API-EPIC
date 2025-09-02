package com.epic.auth.login;

import com.epic.auth.entity.Role;
import com.epic.auth.entity.User;
import com.epic.shared.dto.UserInfoDto;
import com.epic.shared.enums.CommonStatus;
import com.epic.auth.repository.UserRepository;
import com.epic.auth.service.AuthService;
import com.epic.shared.exception.UserInactiveException;
import com.epic.shared.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthLoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User activeUser;
    private User inactiveUser;



    @BeforeEach
    void setup() {

        Role role = new Role();
        role.setId(UUID.fromString("433ed5bb-2766-423d-b445-3ad17a3411a7"));
        role.setRoleName("User");

        activeUser = new User();
        activeUser.setId(UUID.randomUUID());
        activeUser.setEmail("active@test.com");
        activeUser.setPassword("encodedPassword");
        activeUser.setStatus(CommonStatus.Active);
        activeUser.setRole(role);
        activeUser.setFirstName("Active");
        activeUser.setLastName("User");
        activeUser.setBirthdate(LocalDate.of(2000, 1, 1));
        activeUser.setCountry("Portugal");

        inactiveUser = new User();
        inactiveUser.setId(UUID.randomUUID());
        inactiveUser.setEmail("inactive@test.com");
        inactiveUser.setPassword("encodedPassword");
        inactiveUser.setStatus(CommonStatus.Inactive);
    }

    @Test
    void loginSuccessful() {
        when(userRepository.findByEmail("active@test.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(any(UserInfoDto.class))).thenReturn("token123");

        String token = authService.login("active@test.com", "rawPassword");

        assertEquals("token123", token);
        verify(jwtUtil, times(1)).generateToken(any(UserInfoDto.class));
    }

    @Test
    void loginWithInactiveUserThrowsException() {
        when(userRepository.findByEmail("inactive@test.com")).thenReturn(Optional.of(inactiveUser));

        UserInactiveException exception = assertThrows(UserInactiveException.class,
                () -> authService.login("inactive@test.com", "encodedPassword"));

        assertEquals("Inactive Account. Please active it before logging in.", exception.getMessage());
    }

    @Test
    void loginWithWrongPasswordThrowsException() {
        when(userRepository.findByEmail("active@test.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        RuntimeException exception  = assertThrows(RuntimeException.class,
                () -> authService.login("active@test.com", "wrongPassword"));

        assertEquals("Invalid Email or Password", exception.getMessage());
    }

    @Test
    void loginWithNonExistentEmailThrowsException() {
        when(userRepository.findByEmail("noone@test.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login("noone@test.com", "wrongPassword"));

        assertEquals("Invalid Email or Password", exception.getMessage());
    }
}
