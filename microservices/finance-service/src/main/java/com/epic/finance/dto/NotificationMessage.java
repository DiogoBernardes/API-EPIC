package com.epic.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO que representa uma notificação enviada ao utilizador.
 * <p>
 * Contém as informações necessárias para transportar uma notificação
 * dentro do sistema, incluindo o destinatário, tipo, mensagem e timestamp.
 * </p>
 * {@code @Diogo Bernardes}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private UUID userId;
    private String type;
    private String message;
    private String timestamp;
}
