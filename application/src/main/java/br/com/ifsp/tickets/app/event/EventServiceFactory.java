package br.com.ifsp.tickets.app.event;

import br.com.ifsp.tickets.app.event.create.CreateEventUseCase;
import br.com.ifsp.tickets.app.event.create.ICreateEventUseCase;
import br.com.ifsp.tickets.app.event.retrieve.get.GetEventUseCase;
import br.com.ifsp.tickets.app.event.retrieve.get.IGetEventUseCase;
import br.com.ifsp.tickets.app.event.retrieve.search.ISearchEventUseCase;
import br.com.ifsp.tickets.app.event.retrieve.search.SearchEventUseCase;
import br.com.ifsp.tickets.domain.company.ICompanyGateway;
import br.com.ifsp.tickets.domain.event.IEventGateway;

public class EventServiceFactory {

    private static EventService eventService;

    public static EventService create(ICompanyGateway companyGateway, IEventGateway eventGateway) {
        if (eventService == null) {
            final ICreateEventUseCase createEventUseCase = new CreateEventUseCase(companyGateway, eventGateway);
            final IGetEventUseCase getEventUseCase = new GetEventUseCase(eventGateway);
            final ISearchEventUseCase searchEventUseCase = new SearchEventUseCase(eventGateway);
            eventService = new EventService(createEventUseCase, getEventUseCase, searchEventUseCase);
        }
        return eventService;
    }
}
