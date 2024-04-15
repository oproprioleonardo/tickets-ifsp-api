package br.com.ifsp.tickets.app.auth.signup;

import java.util.Date;

public record SignUpInputData(
        String name,
        String email,
        String username,
        String password,
        Date birthDate,
        String cpf,
        String phoneNumber
) {

    public static SignUpInputData of(String name, String email, String username, String password, Date birthDate, String cpf, String phoneNumber) {
        return new SignUpInputData(name, email, username, password, birthDate, cpf, phoneNumber);
    }
}
