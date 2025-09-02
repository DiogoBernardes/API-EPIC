package com.epic.shared.exception;
/**
 * <p>
 * Exceção lançada quando um utilizador não é encontrado no sistema.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String message){
        super(message);
    }
}
