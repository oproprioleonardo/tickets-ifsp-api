package br.com.ifsp.tickets.app.auth;

import br.com.ifsp.tickets.domain.shared.validation.IValidationHandler;
import br.com.ifsp.tickets.domain.user.User;

import java.util.UUID;

public interface IAuthUtils {

    String encrypt(String aPassword);

    void validatePassword(String aPassword, IValidationHandler validationHandler);

    String generateToken(String aSubject);

    boolean isTokenValid(String aToken, User userDetails);

    UUID getUuidFromToken(String aToken);

}
