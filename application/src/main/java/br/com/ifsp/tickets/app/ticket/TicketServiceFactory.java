package br.com.ifsp.tickets.app.ticket;

import br.com.ifsp.tickets.app.ticket.check.CheckTicketUseCase;
import br.com.ifsp.tickets.app.ticket.check.ICheckTicketUseCase;
import br.com.ifsp.tickets.app.ticket.payment.CheckPaymentUseCase;
import br.com.ifsp.tickets.app.ticket.payment.ICheckPaymentUseCase;
import br.com.ifsp.tickets.app.ticket.retrieve.get.GetTicketByIDUseCase;
import br.com.ifsp.tickets.app.ticket.retrieve.get.IGetTicketByIDUseCase;
import br.com.ifsp.tickets.domain.event.IEventGateway;
import br.com.ifsp.tickets.domain.ticket.ITicketGateway;

public class TicketServiceFactory {
    private static TicketService ticketService;

    public static TicketService create(
            IEventGateway eventGateway,
            ITicketGateway ticketGateway
    ) {
        if (ticketService == null) {
            final ICheckTicketUseCase checkTicketUseCase = new CheckTicketUseCase(ticketGateway, eventGateway);
            final IGetTicketByIDUseCase getTicketByIDUseCase = new GetTicketByIDUseCase(ticketGateway, eventGateway);
            final ICheckPaymentUseCase checkPaymentUseCase = new CheckPaymentUseCase(ticketGateway);
            ticketService = new TicketService(checkTicketUseCase, getTicketByIDUseCase, checkPaymentUseCase);
        }
        return ticketService;
    }
}
