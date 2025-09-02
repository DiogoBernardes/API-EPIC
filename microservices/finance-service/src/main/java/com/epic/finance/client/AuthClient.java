package com.epic.finance.client;

import com.epic.shared.dto.UserInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.UUID;

/**
 * Client Feign para comunicação com o Auth Service.
 * <p>
 * Permite que outros microserviços obtenham informações de utilizadores
 * a partir do auth-service de forma remota, utilizando HTTP REST.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */
@FeignClient(name = "auth-service", url = "http://localhost:8082/auth")
public interface AuthClient {

    @GetMapping("/user/id/{id}")
    UserInfoDto getUserById(@PathVariable("id") UUID id);
}
