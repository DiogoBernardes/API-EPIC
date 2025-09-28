package com.epic.finance.service;

import com.epic.finance.dto.NotificationMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Serviço responsável pelo envio de notificações em tempo real para os utilizadores.
 * <p>
 * Utiliza o {@link SimpMessagingTemplate} para publicar mensagens em canais WebSocket
 * específicos de cada utilizador. As notificações são encapsuladas em {@link NotificationMessage},
 * contendo informações como tipo, mensagem e timestamp.
 * </p>
 *
 * Funcionalidades:
 * <ul>
 *     <li>Construir mensagens de notificação com base nos parâmetros recebidos.</li>
 *     <li>Enviar notificações para o canal WebSocket correspondente ao utilizador.</li>
 *     <li>Registar no log a notificação enviada.</li>
 * </ul>
 *
 * {@code @Diogo Bernardes}
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Envia uma notificação em tempo real para um utilizador específico.
     *
     * @param userId  ID do utilizador destinatário.
     * @param type    Tipo da notificação (ex.: BUDGET_WARNING, BUDGET_EXCEEDED).
     * @param message Conteúdo textual da notificação.
     */
    public void sendNotification(UUID userId, String type,  String message) {
        NotificationMessage payload = new NotificationMessage(userId, type, message, Instant.now().toString());
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, payload);
        System.out.println("Sent Notification " + payload);
    }
}
