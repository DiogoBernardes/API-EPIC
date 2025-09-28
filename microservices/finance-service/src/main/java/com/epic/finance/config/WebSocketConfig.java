package com.epic.finance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuração do WebSocket para comunicação em tempo real com suporte a STOMP.
 * <p>
 * Define os endpoints para conexão dos clientes e configura o broker de mensagens
 * responsável por gerir os canais de publicação e subscrição.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Define o prefixo das mensagens de aplicação ({@code /app}).</li>
 *     <li>Ativa um broker simples de memória com prefixo {@code /topic}.</li>
 *     <li>Registra o endpoint {@code /ws} para conexões WebSocket com fallback para SockJS.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura o broker de mensagens do STOMP.
     *
     * @param registry objeto de configuração do broker.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");

        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra o endpoint de conexão WebSocket para os clientes.
     *
     * @param registry objeto de configuração dos endpoints STOMP.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*") // Ajustar para o dominio depois
                .withSockJS();
    }
}
