package br.com.ifsp.tickets.infra.api;

import br.com.ifsp.tickets.domain.shared.search.Pagination;
import br.com.ifsp.tickets.infra.contexts.enrollment.core.models.CreateEnrollmentRequest;
import br.com.ifsp.tickets.infra.contexts.enrollment.core.models.EnrollmentResponse;
import br.com.ifsp.tickets.infra.contexts.enrollment.upsert.models.CreateUpsertEnrollmentRequest;
import br.com.ifsp.tickets.infra.contexts.event.sale.payment.models.CreatePaymentRequest;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/enrollment")
@Tag(name = "Enrollment", description = "Enrollment API - manage enrollments for events")
public interface EnrollmentAPI {

    @PostMapping(consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Enrollment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    ResponseEntity<String> create(@RequestBody CreateEnrollmentRequest request);

    @GetMapping(value = "/list", produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Enrollments retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    ResponseEntity<Pagination<EnrollmentResponse>> findByUser();

    @PostMapping(consumes = "application/json", value = "/upsert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upsert enrollment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    ResponseEntity<String> createUpsertEnrollment(@RequestBody CreateUpsertEnrollmentRequest request);

    @PostMapping(consumes = "application/json", value = "/webhook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Upsert enrollment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    ResponseEntity<Void> webhook(@RequestBody CreatePaymentRequest request);
}
