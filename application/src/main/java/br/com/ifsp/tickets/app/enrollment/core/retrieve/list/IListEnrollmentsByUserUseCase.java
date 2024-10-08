package br.com.ifsp.tickets.app.enrollment.core.retrieve.list;

import br.com.ifsp.tickets.app.IUseCase;
import br.com.ifsp.tickets.app.enrollment.core.retrieve.EnrollmentOutput;
import br.com.ifsp.tickets.domain.shared.search.Pagination;
import br.com.ifsp.tickets.domain.user.User;

public interface IListEnrollmentsByUserUseCase extends IUseCase<User, Pagination<EnrollmentOutput>> {
}
