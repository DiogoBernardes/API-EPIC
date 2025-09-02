package com.epic.shared.exception;

/**
 * <p>
 * Exceção lançada quando um utilizador com status "Inactive" tenta realizar
 * uma ação que requer conta ativa.
 * </p>
 *
 * {@code @Diogo Bernardes}
 */

public class UserInactiveException extends RuntimeException{
    public UserInactiveException(String message){
        super(message);
    }
}
