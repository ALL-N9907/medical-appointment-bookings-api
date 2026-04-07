package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.AvailabilityDTOs;
import edu.unimag.medical.api.dto.DoctorScheduleDTOs;
import edu.unimag.medical.domain.entities.AppointmentType;
import edu.unimag.medical.domain.entities.Doctor;
import edu.unimag.medical.domain.entities.Specialty;
import edu.unimag.medical.domain.repository.AppointmentRepository;
import edu.unimag.medical.domain.repository.AppointmentTypeRepository;
import edu.unimag.medical.domain.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    private DoctorScheduleService doctorScheduleService;

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    private UUID doctorId;
    private UUID appointmentTypeId;
    private UUID specialtyId;
    private Doctor doctor;
    private AppointmentType appointmentType;
    private Specialty specialty;
    private DoctorScheduleDTOs.DoctorScheduleResponse scheduleResponse;
    private LocalDate futureDate;
    private LocalDate today;

    @BeforeEach
    void setUp() {

        doctorId = UUID.randomUUID();
        appointmentTypeId = UUID.randomUUID();
        specialtyId = UUID.randomUUID();
        futureDate = LocalDate.now().plusDays(1);
        today = LocalDate.now();

        specialty = Specialty.builder()
                .id(UUID.randomUUID())
                .name("General Medicine")
                .build();

        doctor = Doctor.builder()
                .fullName("Dr. House")
                .id(doctorId)
                .specialty(specialty)
                .active(true)
                .build();

        appointmentType = AppointmentType.builder()
                .id(appointmentTypeId)
                .durationMinutes(30)
                .build();

        scheduleResponse = new DoctorScheduleDTOs.DoctorScheduleResponse(
                doctorId,
                LocalTime.of(8,0),
                LocalTime.of(18,0),
                today.getDayOfWeek()
        );

    }

    @Test
    @DisplayName("Get availability slots")
    void getAvailabilitySlot() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorScheduleService.findByDoctorId_AndDayOfWeek(eq(doctorId), any(DayOfWeek.class)))
                .thenReturn(scheduleResponse);
        when(appointmentRepository.findByDoctor_IdAndDate(eq(doctorId), eq(futureDate), anyList()))
                .thenReturn(new ArrayList<>());

        when(appointmentTypeRepository.findById(appointmentTypeId)).thenReturn(Optional.of(appointmentType));

        List<AvailabilityDTOs.AvailabilitySlotResponse> slots = availabilityService.getAvailabilitySlot(
                doctorId,
                futureDate,
                appointmentTypeId
        );

        assertNotNull(slots);
        assertEquals(20,slots.size());
        verify(doctorRepository).findById(doctorId);
        verify(doctorScheduleService).findByDoctorId_AndDayOfWeek(eq(doctorId), any(DayOfWeek.class));
        verify(appointmentRepository).findByDoctor_IdAndDate(eq(doctorId), eq(futureDate), anyList());
    }
}