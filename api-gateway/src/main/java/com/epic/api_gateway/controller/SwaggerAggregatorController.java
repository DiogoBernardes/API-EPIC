package com.epic.api_gateway.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.*;

/**
 * Controller para agregação da documentação Swagger/OpenAPI dos microserviços.
 * <p>
 * Esta classe consulta os endpoints da documentação dos microserviços conectados
 * ao API Gateway e consolida os paths e schemas em um único JSON para o Swagger UI.
 * </p>
 * <p>
 * Funcionalidades principais:
 * <ul>
 *     <li>Agrega endpoints de documentação de múltiplos microserviços.</li>
 *     <li>Inclui segurança global via Bearer JWT quando disponível.</li>
 * </ul>
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

@RestController
public class SwaggerAggregatorController {

    private final WebClient webClient;

    public SwaggerAggregatorController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/v3/api-docs/aggregate")
    public Mono<ResponseEntity<Map<String, Object>>> aggregateDocs() {
        Map<String, Object> aggregatedPaths = new HashMap<>();
        Map<String, Object> aggregatedSchemas = new HashMap<>();

        String[] services = {
                "http://localhost:8082/v3/api-docs",
                "http://localhost:8083/v3/api-docs"
        };

        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> (UsernamePasswordAuthenticationToken) securityContext.getAuthentication())
                .map(auth -> "Bearer " + auth.getCredentials())
                .defaultIfEmpty("") // Sem token
                .flatMap(jwtToken -> {
                    List<Mono<Map>> requests = new ArrayList<>();
                    for (String url : services) {
                        Mono<Map> request = webClient.get()
                                .uri(url)
                                .headers(headers -> {
                                    if (!jwtToken.isEmpty()) {
                                        headers.set(HttpHeaders.AUTHORIZATION, jwtToken);
                                    }
                                })
                                .retrieve()
                                .bodyToMono(Map.class)
                                .onErrorResume(e -> {
                                    System.err.println("Error getting docs " + url + ": " + e.getMessage());
                                    return Mono.empty();
                                });
                        requests.add(request);
                    }

                    return Mono.zip(requests, results -> results)
                            .map(results -> {
                                for (Object obj : results) {
                                    if (obj instanceof Map serviceDocs) {
                                        Map<String, Object> paths = (Map<String, Object>) serviceDocs.get("paths");
                                        if (paths != null) aggregatedPaths.putAll(paths);

                                        Map<String, Object> components = (Map<String, Object>) serviceDocs.get("components");
                                        if (components != null && components.get("schemas") != null) {
                                            aggregatedSchemas.putAll((Map<String, Object>) components.get("schemas"));
                                        }
                                    }
                                }

                                Map<String, Object> securitySchemes = Map.of(
                                        "bearerAuth", Map.of(
                                                "type", "http",
                                                "scheme", "bearer",
                                                "bearerFormat", "JWT"
                                        )
                                );

                                Map<String, Object> openApi = new HashMap<>();
                                openApi.put("openapi", "3.0.1");
                                openApi.put("info", Map.of("title", "Epic API Docs", "version", "1.0"));
                                openApi.put("paths", aggregatedPaths);
                                openApi.put("components", Map.of(
                                        "schemas", aggregatedSchemas,
                                        "securitySchemes", securitySchemes
                                ));
                                openApi.put("security", List.of(Map.of("bearerAuth", List.of())));

                                return ResponseEntity.ok(openApi);
                            });
                });
    }
}
