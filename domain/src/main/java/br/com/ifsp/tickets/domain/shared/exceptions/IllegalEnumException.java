package br.com.ifsp.tickets.domain.shared.exceptions;

public class IllegalEnumException extends DomainException{

    public IllegalEnumException(Class<? extends Enum<?>> clazz, int code) {
        super("Invalid enum '%s' by code: %s".formatted(clazz.getSimpleName(), code));
    }

}
