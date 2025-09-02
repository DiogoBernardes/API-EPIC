package com.epic.auth.exception;

/**
 * *<p>
 * Exceção lançada quando o utilizador fornece credenciais inválidas
 * (e-mail ou password incorretos) no processo de login.
 *</p>
 * {@code @Diogo Bernardes}
 */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) { super(message); }
}
