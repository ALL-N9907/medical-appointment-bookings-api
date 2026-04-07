package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.*;
import edu.unimag.medical.domain.entities.*;
import edu.unimag.medical.domain.enums.AppointmentStatus;
import edu.unimag.medical.domain.enums.OfficeStatus;
import edu.unimag.medical.domain.enums.PatientStatus;
import edu.unimag.medical.domain.repository.*;
import edu.unimag.medical.service.mapper.AppointmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentTypeRepository appointmentTypeRepository;

    @Mock
    private OfficeRepository officeRepository;

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    @Mock
    private DoctorScheduleService doctorScheduleService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private UUID patientId;
    private UUID doctorId;
    private UUID officeId;
    private UUID appointmentTypeId;
    private UUID appointmentId;
    private UUID specialtyId;
    private Patient patient;
    private Doctor doctor;
    private Office office;
    private Specialty specialty;
    private AppointmentType appointmentType;
    private DoctorSchedule doctorSchedule;
    private Appointment appointment;
    private AppointmentDTOs.CreateAppointmentRequest createRequest;
    private AppointmentDTOs.AppointmentResponse appointmentResponse;
    private LocalDateTime futureStartAt;
    private LocalDateTime futureEndAt;
    private LocalDate futureDate;

    @BeforeEach
    void setUp() {

        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        officeId = UUID.randomUUID();
        appointmentTypeId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        specialtyId = UUID.randomUUID();

        futureDate = LocalDate.now().plusDays(1);
        futureStartAt = LocalDateTime.of(futureDate, LocalTime.of(10,0));
        futureEndAt = futureStartAt.plusMinutes(30);

        specialty = Specialty.builder()
                .id(UUID.randomUUID())
                .name("General Medicine")
                .build();

        patient = Patient.builder()
                .id(patientId)
                .fullName("John Doe")
                .patientStatus(PatientStatus.ACTIVE)
                .build();

        doctor = Doctor.builder()
                .fullName("Dr. House")
                .id(doctorId)
                .specialty(specialty)
                .active(true)
                .build();

        office = Office.builder()
                .id(officeId)
                .officeStatus(OfficeStatus.AVAILABLE)
                .build();

        appointmentType = AppointmentType.builder()
                .id(appointmentTypeId)
                .durationMinutes(30)
                .build();

        doctorSchedule = DoctorSchedule.builder()
                .startAt(LocalTime.of(8, 0))
                .endAt(LocalTime.of(18, 0))
                .build();

        createRequest = new AppointmentDTOs.CreateAppointmentRequest(
                        patientId, doctorId, officeId, appointmentTypeId,
                futureStartAt, futureDate
                );

        appointment = Appointment.builder()
                .id(appointmentId)
                .startAt(futureStartAt)
                .endAt(futureEndAt)
                .appointmentStatus(AppointmentStatus.SCHEDULED)
                .build();

        PatientDTOs.PatientResponse patientResponse = new PatientDTOs.PatientResponse(
                patientId,
                patient.getFullName(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getPatientStatus()

        );

        DoctorDTOs.DoctorResponse doctorResponse = new DoctorDTOs.DoctorResponse(
                doctorId,
                doctor.getFullName(),
                doctor.isActive(),
                doctor.getSpecialty().getName()


        );

        OfficeDTOs.OfficeResponse officeResponse = new OfficeDTOs.OfficeResponse(
                officeId,
                office.getNumber(),
                office.getLocation(),
                office.getOfficeStatus()

        );

        AppointmentTypeDTOs.AppointmentTypeResponse appointmentTypeResponse = new AppointmentTypeDTOs.AppointmentTypeResponse(
                appointmentTypeId,
                appointmentType.getName(),
                appointmentType.getDurationMinutes()
        );

        appointmentResponse = new AppointmentDTOs.AppointmentResponse(
                appointmentId,
                patientResponse,
                doctorResponse,
                officeResponse,
                appointmentTypeResponse,
                futureStartAt,
                futureEndAt,
                futureDate,
                AppointmentStatus.SCHEDULED,
                null,
                null
        );
    }


    @Test
    @DisplayName("Create appointment test")
    void createAppointment() {
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(officeRepository.findById(officeId)).thenReturn(Optional.of(office));
        when(appointmentTypeRepository.findById(appointmentTypeId)).thenReturn(Optional.of(appointmentType));

        when(doctorScheduleRepository.findByDoctor_IdAndDayOfWeek(eq(doctorId), any(DayOfWeek.class)))
                .thenReturn(Optional.of(doctorSchedule));

        when(appointmentRepository.existsOverlappingForDoctor(any(), any(), any(), any(), any())).thenReturn(false);
        when(appointmentRepository.existsOverlappingForOffice(any(), any(), any(), any(), any())).thenReturn(false);
        when(appointmentRepository.existsOverlappingForPatient(any(), any(), any(), any(), any())).thenReturn(false);


        when(appointmentMapper.toEntity(eq(createRequest), eq(patient), eq(doctor), eq(office), eq(appointmentType)))
                .thenReturn(appointment);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponse(appointment)).thenReturn(appointmentResponse);


        AppointmentDTOs.AppointmentResponse response = appointmentService.createAppointment(createRequest);

        assertNotNull(response);
        assertEquals(appointmentId, response.id());
        verify(appointmentRepository, times(1)).save(appointment);

    }

    @Test
    @DisplayName("Find by id")
    void findByid() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(appointmentResponse);

        AppointmentDTOs.AppointmentResponse response = appointmentService.findByid(appointmentId);

        assertNotNull(response);
        assertEquals(appointmentId, response.id());
        verify(appointmentRepository, times(1)).findById(appointmentId);
    }

    @Test
    @DisplayName("Find All")
    void findAll() {
        List<Appointment> appointments = List.of(appointment);
        List<AppointmentDTOs.AppointmentResponse> appointmentResponses = List.of(appointmentResponse);

        when(appointmentRepository.findAll()).thenReturn(appointments);
        when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(appointmentResponse);

        List<AppointmentDTOs.AppointmentResponse> result = appointmentService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointmentId, result.get(0).id());
        verify(appointmentRepository).findAll();

    }

    @Test
    @DisplayName("Confirm Appointment")
    void confirmAppointment() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(appointmentResponse);

        AppointmentDTOs.AppointmentResponse response = appointmentService.confirmAppointment(appointmentId);

        assertNotNull(response);
        assertEquals(AppointmentStatus.CONFIRMED, appointment.getAppointmentStatus());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("Cancel appointment")
    void cancelAppointment() {
        AppointmentDTOs.CancelAppointmentRequest cancelRequest =
                new AppointmentDTOs.CancelAppointmentRequest("Patient requested cancellation");

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(appointmentResponse);

        AppointmentDTOs.AppointmentResponse response =
                appointmentService.cancelAppointment(appointmentId, cancelRequest);

        assertNotNull(response);
        assertEquals(AppointmentStatus.CANCELLED, appointment.getAppointmentStatus());
        assertEquals("Patient requested cancellation", appointment.getCancellationReason());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("Complete appointment")
    void completeAppointment() {

        appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
        appointment.setStartAt(LocalDateTime.now().minusHours(1));
        AppointmentDTOs.CompleteAppointmentRequest completeRequest =
                new AppointmentDTOs.CompleteAppointmentRequest("Treatment completed successfully");

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(appointmentResponse);

        AppointmentDTOs.AppointmentResponse response = appointmentService.completeAppointment(appointmentId, completeRequest);

        assertNotNull(response);
        assertEquals(AppointmentStatus.COMPLETED, appointment.getAppointmentStatus());
        assertEquals("Treatment completed successfully", appointment.getObservation());
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("Set as no Show appointmet")
    void setAsNoShowAppointment() {
        appointment.setStartAt(LocalDateTime.now().minusHours(1));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toResponse(any(Appointment.class))).thenReturn(appointmentResponse);

        AppointmentDTOs.AppointmentResponse response = appointmentService.setAsNoShowAppointment(appointmentId);

        assertNotNull(response);
        assertEquals(AppointmentStatus.NO_SHOW, appointment.getAppointmentStatus());
        verify(appointmentRepository).save(appointment);
    }
}