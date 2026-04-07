package edu.unimag.medical.service;

import edu.unimag.medical.api.dto.DoctorScheduleDTOs;
import edu.unimag.medical.domain.entities.Doctor;
import edu.unimag.medical.domain.entities.DoctorSchedule;
import edu.unimag.medical.domain.entities.Specialty;
import edu.unimag.medical.domain.repository.DoctorRepository;
import edu.unimag.medical.domain.repository.DoctorScheduleRepository;
import edu.unimag.medical.service.mapper.DoctorScheduleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DoctorScheduleServiceImplTest {

    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorScheduleMapper doctorScheduleMapper;

    @InjectMocks
    private DoctorScheduleServiceImpl doctorScheduleService;

    private UUID doctorId;
    private UUID scheduleId;
    private UUID specialityId;
    private Doctor doctor;
    private Specialty specialty;
    private DoctorSchedule doctorSchedule;
    private DoctorScheduleDTOs.CreateDoctorScheduleRequest createRequest;
    private DoctorScheduleDTOs.DoctorScheduleResponse scheduleResponse;
    private DayOfWeek dayOfWeek;

    @BeforeEach
    void setUp() {

        doctorId = UUID.randomUUID();
        scheduleId = UUID.randomUUID();
        specialityId = UUID.randomUUID();
        dayOfWeek = DayOfWeek.MONDAY;

        doctor = Doctor.builder()
                .fullName("Dr. House")
                .id(doctorId)
                .specialty(specialty)
                .active(true)
                .build();

        doctorSchedule = DoctorSchedule.builder()
                .id(scheduleId)
                .doctor(doctor)
                .dayOfWeek(dayOfWeek)
                .startAt(LocalTime.of(9, 0))
                .endAt(LocalTime.of(17, 0))
                .build();

        createRequest = new DoctorScheduleDTOs.CreateDoctorScheduleRequest(
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                dayOfWeek

        );

        scheduleResponse = new DoctorScheduleDTOs.DoctorScheduleResponse(
                scheduleId,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                dayOfWeek
        );
    }

    @Test
    @DisplayName("Create")
    void create() {
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorScheduleRepository.existsByDoctor_IdAndDayOfWeek(eq(doctorId), eq(dayOfWeek))).thenReturn(false);
        when(doctorScheduleMapper.toEntity(any(DoctorScheduleDTOs.CreateDoctorScheduleRequest.class), any(Doctor.class))).thenReturn(doctorSchedule);
        when(doctorScheduleRepository.save(any(DoctorSchedule.class))).thenReturn(doctorSchedule);
        when(doctorScheduleMapper.toResponse(any(DoctorSchedule.class))).thenReturn(scheduleResponse);

        DoctorScheduleDTOs.DoctorScheduleResponse response = doctorScheduleService.create(doctorId, createRequest);

        assertNotNull(response);
        assertEquals(scheduleId, response.id());
        assertEquals(dayOfWeek, response.dayOfWeek());
        assertEquals(LocalTime.of(9, 0), response.startAt());
        assertEquals(LocalTime.of(17, 0), response.endAt());

        verify(doctorRepository).findById(doctorId);
        verify(doctorScheduleRepository).existsByDoctor_IdAndDayOfWeek(doctorId, dayOfWeek);
        verify(doctorScheduleMapper).toEntity(any(DoctorScheduleDTOs.CreateDoctorScheduleRequest.class), any(Doctor.class));
        verify(doctorScheduleRepository).save(any(DoctorSchedule.class));
        verify(doctorScheduleMapper).toResponse(any(DoctorSchedule.class));
    }

    @Test
    @DisplayName("Find by doctor and day of week")
    void findByDoctorId_AndDayOfWeek() {
        DayOfWeek tuesday = DayOfWeek.TUESDAY;

        DoctorSchedule tuesdaySchedule = DoctorSchedule.builder()
                .id(UUID.randomUUID())
                .doctor(doctor)
                .dayOfWeek(tuesday)
                .startAt(LocalTime.of(10, 0))
                .endAt(LocalTime.of(18, 0))
                .build();

        DoctorScheduleDTOs.DoctorScheduleResponse tuesdayResponse = new DoctorScheduleDTOs.DoctorScheduleResponse(
                tuesdaySchedule.getId(),
                LocalTime.of(10, 0),
                LocalTime.of(18, 0),
                tuesday
        );

        when(doctorScheduleRepository.findByDoctor_IdAndDayOfWeek(eq(doctorId), eq(tuesday)))
                .thenReturn(Optional.of(tuesdaySchedule));
        when(doctorScheduleMapper.toResponse(any(DoctorSchedule.class))).thenReturn(tuesdayResponse);

        DoctorScheduleDTOs.DoctorScheduleResponse response = doctorScheduleService.findByDoctorId_AndDayOfWeek(doctorId, tuesday);

        assertNotNull(response);
        assertEquals(tuesday, response.dayOfWeek());
        assertEquals(LocalTime.of(10, 0), response.startAt());
        assertEquals(LocalTime.of(18, 0), response.endAt());

        verify(doctorScheduleRepository).findByDoctor_IdAndDayOfWeek(doctorId, tuesday);
    }

    @Test
    @DisplayName("Find all")
    void findAll() {
        List<DoctorSchedule> schedules = List.of(doctorSchedule);
        List<DoctorScheduleDTOs.DoctorScheduleResponse> responses = List.of(scheduleResponse);

        when(doctorScheduleRepository.findByDoctor_id(doctorId)).thenReturn(schedules);
        when(doctorScheduleMapper.toResponse(any(DoctorSchedule.class))).thenReturn(scheduleResponse);

        List<DoctorScheduleDTOs.DoctorScheduleResponse> result = doctorScheduleService.findAll(doctorId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(scheduleId, result.get(0).id());
        assertEquals(dayOfWeek, result.get(0).dayOfWeek());
        assertEquals(LocalTime.of(9, 0), result.get(0).startAt());
        assertEquals(LocalTime.of(17, 0), result.get(0).endAt());

        verify(doctorScheduleRepository).findByDoctor_id(doctorId);
        verify(doctorScheduleMapper).toResponse(any(DoctorSchedule.class));

    }
}