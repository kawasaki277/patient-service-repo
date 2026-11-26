package com.clinicbooking.patient.infrastructure.in;

import com.clinicbooking.patient.domain.DomainException;
import com.clinicbooking.patient.domain.NotFoundException;
import com.clinicbooking.patient.infrastructure.in.rest.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MissingServletRequestParameterException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFoundMapsTo404() {
        ProblemDetail p = handler.handleNotFound(new NotFoundException("no existe"));
        assertEquals(404, p.getStatus());
        assertEquals("no existe", p.getDetail());
    }

    @Test
    void domainErrorMapsTo422() {
        assertEquals(422, handler.handleDomain(new DomainException("regla")).getStatus());
    }

    @Test
    void badRequestMapsTo400() {
        ProblemDetail p = handler.handleBadRequest(new MissingServletRequestParameterException("date", "LocalDate"));
        assertEquals(400, p.getStatus());
    }
}
