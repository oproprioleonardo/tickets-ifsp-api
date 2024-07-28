package br.com.ifsp.tickets.app.auth.signup;

import br.com.ifsp.tickets.app.auth.IAuthUtils;
import br.com.ifsp.tickets.domain.communication.PlaceHolder;
import br.com.ifsp.tickets.domain.communication.email.Email;
import br.com.ifsp.tickets.domain.communication.email.IEmailGateway;
import br.com.ifsp.tickets.domain.communication.message.IMessageGateway;
import br.com.ifsp.tickets.domain.communication.message.Message;
import br.com.ifsp.tickets.domain.communication.message.type.MessageSubject;
import br.com.ifsp.tickets.domain.communication.message.type.MessageType;
import br.com.ifsp.tickets.domain.shared.exceptions.NotFoundException;
import br.com.ifsp.tickets.domain.shared.validation.handler.Notification;
import br.com.ifsp.tickets.domain.user.IUserGateway;
import br.com.ifsp.tickets.domain.user.User;
import br.com.ifsp.tickets.domain.user.email.IUpsertEmailGateway;
import br.com.ifsp.tickets.domain.user.email.UpsertEmail;
import br.com.ifsp.tickets.domain.user.vo.CPF;
import br.com.ifsp.tickets.domain.user.vo.EmailAddress;
import br.com.ifsp.tickets.domain.user.vo.PhoneNumber;
import br.com.ifsp.tickets.domain.user.vo.role.Role;

import java.time.LocalDate;

public class SignUpUseCase implements ISignUpUseCase {

    private final IUserGateway userGateway;
    private final IAuthUtils authUtils;
    private final IUpsertEmailGateway upsertEmailGateway;
    private final IMessageGateway messageGateway;
    private final IEmailGateway emailGateway;

    public SignUpUseCase(IUserGateway userGateway, IAuthUtils authUtils, IUpsertEmailGateway upsertEmailGateway, IMessageGateway messageGateway, IEmailGateway emailGateway) {
        this.userGateway = userGateway;
        this.authUtils = authUtils;
        this.upsertEmailGateway = upsertEmailGateway;
        this.messageGateway = messageGateway;
        this.emailGateway = emailGateway;
    }

    @Override
    public SignUpOutput execute(SignUpInput anIn) {
        final String name = anIn.name();
        final String username = anIn.username();
        final CPF cpf = new CPF(anIn.cpf());
        final EmailAddress emailAddress = new EmailAddress(anIn.email());
        final PhoneNumber phoneNumber = new PhoneNumber(anIn.phoneNumber());
        final LocalDate birthDate = anIn.birthDate();
        final String password = anIn.password();
        final String passwordEncoded = this.authUtils.encrypt(password);

        User user = User.create(
                name,
                Role.CUSTOMER,
                phoneNumber,
                username,
                passwordEncoded,
                cpf,
                birthDate
        );

        final Notification notification = Notification.create("Could not create aggregate User");
        user.validate(notification);

        notification
                .throwPossibleErrors()
                .uniqueness(() -> this.userGateway.existsByUsername(username), "username")
                .uniqueness(() -> this.userGateway.existsByEncryptedCPF(cpf), "cpf")
                .uniqueness(() -> this.userGateway.existsByEmail(emailAddress), "email")
                .uniqueness(() -> this.upsertEmailGateway.existsByEmail(emailAddress), "email")
                .throwPossibleErrors();

        this.authUtils.validatePassword(password, notification);
        notification.throwPossibleErrors();
        user = this.userGateway.create(user);

        // e-mail de confirmação para ativação de conta
        final UpsertEmail upsertEmail = UpsertEmail.create(emailAddress, user);
        upsertEmail.userNotified();
        upsertEmail.validate(notification);
        notification.throwPossibleErrors();
        this.upsertEmailGateway.create(upsertEmail);

        this.sendEmail(notification, emailAddress, user, upsertEmail);

        return SignUpOutput.from(user);
    }

    private void sendEmail(Notification notification, EmailAddress emailAddress, User user, UpsertEmail upsertEmail) {
        final Message message = this.messageGateway
                .findBySubjectAndType(MessageSubject.ACCOUNT_ACTIVATION, MessageType.HTML)
                .orElseThrow(() -> NotFoundException.with("Message not found with subject: " + MessageSubject.ACCOUNT_ACTIVATION + " and type: " + MessageType.HTML));

        final Email email = Email.create(emailAddress.getValue(), message);
        email.validate(notification);
        notification.throwPossibleErrors();

        email.with(
                PlaceHolder.of("token", upsertEmail.getToken()),
                PlaceHolder.of("user_name", user.getName()),
                PlaceHolder.of("user_email", emailAddress.getValue())
        );

        this.emailGateway.create(email);
    }
}
