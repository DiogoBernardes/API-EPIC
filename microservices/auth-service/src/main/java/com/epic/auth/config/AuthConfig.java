package com.epic.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.UUID;

/**
 * <p>
 * Mapeia configurações definidas no `application.yml` sob o prefixo <code>auth</code>.
 * Atualmente armazena o ID da Role padrão para novos utilizadores.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthConfig {
    private UUID userRoleId;
}
