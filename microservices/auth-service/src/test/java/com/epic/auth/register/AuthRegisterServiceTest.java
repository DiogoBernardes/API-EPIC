package com.epic.auth.register;

import com.epic.auth.entity.Role;
import com.epic.auth.entity.User;
import com.epic.shared.dto.UserInfoDto;
import com.epic.shared.enums.CommonStatus;
import com.epic.auth.repository.UserRepository;
import com.epic.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AuthRegisterServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User activeUser;

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
        activeUser.setBirthdate(LocalDate.of(1990, 1, 1));
        activeUser.setCountry("Portugal");
    }

    @Test
    void registerSuccessful() {
        UserInfoDto dto = UserInfoDto.builder()
                .email("newuser@test.com")
                .firstName("New")
                .lastName("User")
                .birthDate(LocalDate.of(2000, 1, 1))
                .country("Portugal")
                .build();

        when(userRepository.findByEmail("newuser@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("myPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = authService.register(dto, "myPassword");

        assertEquals("newuser@test.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals("Active", savedUser.getStatus());
        assertEquals("New", savedUser.getFirstName());
        assertEquals("User", savedUser.getLastName());
        assertEquals("Portugal", savedUser.getCountry());
        assertNotNull(savedUser.getRole().getId());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerWithExistingEmailThrowsException() {
        UserInfoDto dto = UserInfoDto.builder()
                .email("active@test.com")
                .build();

        when(userRepository.findByEmail("active@test.com")).thenReturn(Optional.of(activeUser));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(dto, "anyPassword"));

        assertEquals("Email Already Exists", exception.getMessage());
    }
}
