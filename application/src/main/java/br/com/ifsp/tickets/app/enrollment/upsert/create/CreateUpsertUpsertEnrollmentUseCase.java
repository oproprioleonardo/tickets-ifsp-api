package br.com.ifsp.tickets.app.enrollment.upsert.create;

import br.com.ifsp.tickets.app.enrollment.ITicketQRGenerator;
import br.com.ifsp.tickets.app.enrollment.core.create.CreateEnrollmentOutput;
import br.com.ifsp.tickets.domain.communication.email.Email;
import br.com.ifsp.tickets.domain.communication.email.IEmailGateway;
import br.com.ifsp.tickets.domain.communication.message.IMessageGateway;
import br.com.ifsp.tickets.domain.communication.message.Message;
import br.com.ifsp.tickets.domain.communication.message.type.MessageSubject;
import br.com.ifsp.tickets.domain.communication.message.type.MessageType;
import br.com.ifsp.tickets.domain.company.Company;
import br.com.ifsp.tickets.domain.company.ICompanyGateway;
import br.com.ifsp.tickets.domain.enrollment.Enrollment;
import br.com.ifsp.tickets.domain.enrollment.IEnrollmentGateway;
import br.com.ifsp.tickets.domain.enrollment.upsert.IUpsertEnrollmentGateway;
import br.com.ifsp.tickets.domain.enrollment.upsert.UpsertEnrollment;
import br.com.ifsp.tickets.domain.event.Event;
import br.com.ifsp.tickets.domain.event.EventID;
import br.com.ifsp.tickets.domain.event.EventStatus;
import br.com.ifsp.tickets.domain.event.IEventGateway;
import br.com.ifsp.tickets.domain.event.sale.ITicketSaleGateway;
import br.com.ifsp.tickets.domain.event.sale.TicketSale;
import br.com.ifsp.tickets.domain.event.sale.TicketSaleID;
import br.com.ifsp.tickets.domain.shared.exceptions.NotFoundException;
import br.com.ifsp.tickets.domain.shared.file.IFileStorage;
import br.com.ifsp.tickets.domain.shared.validation.handler.Notification;
import br.com.ifsp.tickets.domain.ticket.ITicketGateway;
import br.com.ifsp.tickets.domain.ticket.Ticket;
import br.com.ifsp.tickets.domain.ticket.TicketID;
import br.com.ifsp.tickets.domain.user.User;
import br.com.ifsp.tickets.domain.user.UserID;

import java.time.LocalDate;

public class CreateUpsertUpsertEnrollmentUseCase implements ICreateUpsertEnrollmentUseCase {
    private final IEventGateway eventGateway;
    private final IEnrollmentGateway enrollmentGateway;
    private final IUpsertEnrollmentGateway upsertEnrollmentGateway;
    private final ITicketGateway ticketGateway;
    private final ITicketSaleGateway ticketSaleGateway;
    private final IMessageGateway messageGateway;
    private final ICompanyGateway companyGateway;
    private final IEmailGateway emailGateway;
    private final IFileStorage fileProvider;
    private final ITicketQRGenerator ticketGenerator;

    public CreateUpsertUpsertEnrollmentUseCase(IEventGateway eventGateway, IEnrollmentGateway enrollmentGateway, IUpsertEnrollmentGateway upsertEnrollmentGateway, ITicketGateway ticketGateway, ITicketSaleGateway ticketSaleGateway, IMessageGateway messageGateway, ICompanyGateway companyGateway, IEmailGateway emailGateway, IFileStorage fileProvider, ITicketQRGenerator ticketGenerator) {
        this.eventGateway = eventGateway;
        this.enrollmentGateway = enrollmentGateway;
        this.upsertEnrollmentGateway = upsertEnrollmentGateway;
        this.ticketGateway = ticketGateway;
        this.ticketSaleGateway = ticketSaleGateway;
        this.messageGateway = messageGateway;
        this.companyGateway = companyGateway;
        this.emailGateway = emailGateway;
        this.fileProvider = fileProvider;
        this.ticketGenerator = ticketGenerator;
    }

    @Override
    public String execute(CreateUpsertEnrollmentInput anIn) {
        final User user = anIn.user();
        String preference = anIn.preferenceURL();
        String name = anIn.name();
        String emailString = anIn.email();
        LocalDate birthDate = anIn.birthDate();
        String document = anIn.document();
        TicketID ticketID = TicketID.with(anIn.ticketID());
        final boolean alreadyExists;
        final EventID eventID = EventID.with(anIn.eventId());
        final TicketSaleID ticketSaleID = TicketSaleID.with(anIn.ticketSaleId());
        final TicketSale ticketSale = this.ticketSaleGateway.findById(ticketSaleID).orElseThrow(() -> NotFoundException.with(TicketSale.class, ticketSaleID));
        final Event event = this.eventGateway.findById(eventID).orElseThrow(() -> NotFoundException.with(Event.class, eventID));
        final Company company = this.companyGateway.findById(event.getCompanyID()).orElseThrow(() -> NotFoundException.with(Company.class, event.getCompanyID()));

        if (!event.getStatus().equals(EventStatus.OPENED))
            Notification.create("Event is not opened").append("Event is not open for enrollment").throwPossibleErrors();

        if (user != null) {
            name = user.getName();
            emailString = user.getEmail().getValue();
            birthDate = user.getBirthDate();
            document = user.getDocument().getValue();
            alreadyExists = this.enrollmentGateway.existsByUserIDAndEventID(user.getId(), eventID);
        } else alreadyExists = this.enrollmentGateway.existsByDocumentAndEventID(document, eventID);

        if (alreadyExists) {
            Notification.create("Validation Error").append("User already enrolled in this event").throwPossibleErrors();
        }
        final UserID userID = user != null ? user.getId() : new UserID(null);


        final UpsertEnrollment enrollment = UpsertEnrollment
                .newUpsertEnrollment(name, emailString, document, birthDate, userID, eventID, ticketSaleID, ticketID);

        final Notification notification = Notification.create();
        enrollment.validate(notification);
        notification.throwPossibleErrors();
        final UpsertEnrollment createdEnrollment = this.upsertEnrollmentGateway.create(enrollment);

        return createdEnrollment.getId().getValue().toString();
    }
}
