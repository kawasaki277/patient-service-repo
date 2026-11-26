package com.clinicbooking.patient.infrastructure.in;

import com.clinicbooking.patient.application.port.in.DoctorUseCase;
import com.clinicbooking.patient.domain.BlockKind;
import com.clinicbooking.patient.domain.Doctor;
import com.clinicbooking.patient.domain.ScheduleBlock;
import com.clinicbooking.patient.infrastructure.in.rest.DoctorController;
import com.clinicbooking.patient.infrastructure.in.rest.dto.DoctorDtos.BlockScheduleRequest;
import com.clinicbooking.patient.infrastructure.in.rest.dto.DoctorDtos.BookingRequest;
import com.clinicbooking.patient.infrastructure.in.rest.dto.DoctorDtos.CreateDoctorRequest;
import com.clinicbooking.patient.infrastructure.in.rest.dto.DoctorDtos.SlotsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DoctorControllerTest {

    private static final LocalDate DAY = LocalDate.of(2026, 11, 23);

    @Mock DoctorUseCase useCase;

    private DoctorController controller() {
        return new DoctorController(useCase);
    }

    @Test
    void createMapsRequestToActiveDoctor() {
        when(useCase.create(any())).thenAnswer(i -> ((Doctor) i.getArgument(0)).withId(1L));
        Doctor d = controller().create(new CreateDoctorRequest("Dr. A", "Pediatría", LocalTime.of(8, 0), LocalTime.of(12, 0), 20));
        assertEquals(1L, d.id());
        assertTrue(d.active());
    }

    @Test
    void getDelegates() {
        Doctor d = new Doctor(1L, "A", "B", true, LocalTime.of(8, 0), LocalTime.of(9, 0), 30);
        when(useCase.get(1L)).thenReturn(d);
        assertEquals(d, controller().get(1L));
    }

    @Test
    void availableSlotsWrapsResult() {
        List<LocalDateTime> slots = List.of(DAY.atTime(8, 0));
        when(useCase.availableSlots(1L, DAY)).thenReturn(slots);
        SlotsResponse r = controller().availableSlots(1L, DAY);
        assertEquals(slots, r.slots());
        assertEquals(DAY, r.date());
    }

    @Test
    void blockScheduleDelegates() {
        ScheduleBlock b = new ScheduleBlock(1L, 1L, DAY.atTime(8, 0), DAY.atTime(9, 0), BlockKind.BLOCKED, "x", null);
        when(useCase.blockSchedule(1L, DAY.atTime(8, 0), DAY.atTime(9, 0), "x")).thenReturn(b);
        assertEquals(b, controller().blockSchedule(1L, new BlockScheduleRequest(DAY.atTime(8, 0), DAY.atTime(9, 0), "x")));
    }

    @Test
    void searchAndBookingEndpointsDelegate() {
        when(useCase.findBySpecialtyAndDate("Pediatría", DAY, DAY)).thenReturn(List.of());
        assertTrue(controller().bySpecialtyAndDate("Pediatría", DAY, DAY).isEmpty());

        controller().registerBooking(1L, new BookingRequest(5L, DAY.atTime(8, 0), DAY.atTime(8, 30)));
        verify(useCase).registerBooking(1L, 5L, DAY.atTime(8, 0), DAY.atTime(8, 30));

        controller().releaseBooking(5L);
        verify(useCase).releaseBooking(5L);
    }
}
