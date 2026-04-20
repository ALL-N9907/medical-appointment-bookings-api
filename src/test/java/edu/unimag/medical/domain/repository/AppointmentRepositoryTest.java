package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.*;
import edu.unimag.medical.domain.enums.AppointmentStatus;
import edu.unimag.medical.domain.enums.OfficeStatus;
import edu.unimag.medical.domain.enums.PatientStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class AppointmentRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Doctor doctor;
    private Patient patient;
    private Office office;
    private Specialty specialty;
    private AppointmentType appointmentType;
    private Appointment appointment1;
    private Appointment appointment2;
    private Appointment appointment3;
    private Appointment appointment4;

    @BeforeEach
    void setUp() {

        specialty = Specialty.builder()
                .name("Psychology")
                .build();
        testEntityManager.persist(specialty);

        doctor = Doctor.builder()
                .fullName("Dr. House")
                .active(true)
                .specialty(specialty)
                .build();
        testEntityManager.persist(doctor);

        patient = Patient.builder()
                .fullName("John Doe")
                .email("johndoe@email.com")
                .phone("1111111111")
                .patientStatus(PatientStatus.ACTIVE)
                .build();
        testEntityManager.persist(patient);

        office = Office.builder()
                .number(101)
                .location("Norte")
                .officeStatus(OfficeStatus.AVAILABLE)
                .build();
        testEntityManager.persist(office);

        appointmentType = AppointmentType.builder()
                .name("General Consultation")
                .durationMinutes(30)
                .build();
        testEntityManager.persist(appointmentType);

        appointment1 = Appointment.builder()
                .appointmentStatus(AppointmentStatus.SCHEDULED)
                .startAt(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0)))
                .endAt(LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 30)))
                .date(LocalDate.now())
                .patient(patient)
                .office(office)
                .doctor(doctor)
                .appointmentType(appointmentType)
                .observation("First psychiatric consultation")
                .build();

        appointment2 = Appointment.builder()
                .appointmentStatus(AppointmentStatus.SCHEDULED)
                .startAt(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(11, 0)))
                .endAt(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(11, 30)))
                .date(LocalDate.now().plusDays(1))
                .patient(patient)
                .office(office)
                .doctor(doctor)
                .appointmentType(appointmentType)
                .observation("Treatment follow-up")
                .build();

        appointment3 = Appointment.builder()
                .appointmentStatus(AppointmentStatus.COMPLETED)
                .startAt(LocalDateTime.of(LocalDate.now().minusDays(3), LocalTime.of(9, 0)))
                .endAt(LocalDateTime.of(LocalDate.now().minusDays(3), LocalTime.of(9, 30)))
                .date(LocalDate.now().minusDays(3))
                .patient(patient)
                .office(office)
                .doctor(doctor)
                .appointmentType(appointmentType)
                .observation("initial diagnosis")
                .build();

        appointment4 = Appointment.builder()
                .appointmentStatus(AppointmentStatus.CANCELLED)
                .startAt(LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(14, 0)))
                .endAt(LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.of(14, 30)))
                .date(LocalDate.now().plusDays(2))
                .patient(patient)
                .office(office)
                .doctor(doctor)
                .appointmentType(appointmentType)
                .cancellationReason("Patient cancelled for personal reasons")
                .observation("It will be rescheduled for next week")
                .build();


        testEntityManager.persist(appointment1);
        testEntityManager.persist(appointment2);
        testEntityManager.persist(appointment3);
        testEntityManager.persist(appointment4);
        testEntityManager.flush();

    }

    @Test
    @DisplayName("Find appointments by patient id and status")
    void findByPatient_IdAndAppointmentStatus() {
        List<Appointment> scheduledAppointments = appointmentRepository.findByPatient_IdAndAppointmentStatus(
                patient.getId(), AppointmentStatus.SCHEDULED
        );

        assertThat(scheduledAppointments).hasSize(2);
        assertThat(scheduledAppointments).allMatch(a -> a.getAppointmentStatus() == AppointmentStatus.SCHEDULED);
        assertThat(scheduledAppointments.getFirst().getPatient().getId()).isEqualTo(patient.getId());
    }

    @Test
    @DisplayName("Find appointments by date between")
    void findByDateBetween() {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);

        List<Appointment> appointments = appointmentRepository.findByDateBetween(start, end);

        assertThat(appointments).hasSize(3);
        assertThat(appointments).extracting(Appointment::getDate)
                .allMatch(date -> !date.isBefore(start) && !date.isAfter(end));
    }

    @Test
    @DisplayName("Find appointments by doctor and range of time")
    void findByRange() {

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);

        List<Appointment> overlappingAppointments = appointmentRepository.findByRange(start, end);

        assertThat(overlappingAppointments).hasSize(3);
        assertThat(overlappingAppointments).extracting(Appointment::getDate)
                .containsExactlyInAnyOrder(
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(2)
                );

    }

    @Test
    @DisplayName("Find appointments that are overlapping by office")
    void findOverlappingByOffice() {
        boolean overlapping = appointmentRepository.existsOverlappingForOffice(
                office.getId(),
                LocalDate.now(),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 15)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 45)),
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.COMPLETED)
        );

        assertThat(overlapping).isTrue();

    }

    @Test
    @DisplayName("Find appointments by doctor id and date")
    void findByDoctor_IdAndDate() {

        List<AppointmentStatus> statuses = List.of(
                AppointmentStatus.SCHEDULED,
                AppointmentStatus.COMPLETED,
                AppointmentStatus.CANCELLED,
                AppointmentStatus.NO_SHOW
        );
        List<Appointment> appointments = appointmentRepository.findByDoctor_IdAndDate(
                doctor.getId(), LocalDate.now(), statuses
        );

        assertThat(appointments).hasSize(1);
        assertThat(appointments.getFirst().getStartAt().toLocalTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(appointments.getFirst().getDoctor().getId()).isEqualTo(doctor.getId());

    }

    @Test
    @DisplayName("Calculate office occupancy by date range")
    void findOfficeOccupancyByDateRange() {


        LocalDateTime start = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime end   = LocalDate.now().plusDays(3).atTime(LocalTime.MAX);

        List<Object[]> occupancy = appointmentRepository.findOfficeOccupancyByDateRange(start, end);

        assertThat(occupancy).isNotEmpty();

        Object[] officeOccupancy = occupancy.stream()
                .filter(data -> data[0].equals(office.getId()))
                .findFirst()
                .orElse(null);

        assertThat(officeOccupancy).isNotNull();

        assertThat((Long) officeOccupancy[1]).isEqualTo(3);
        assertThat(officeOccupancy[0]).isInstanceOf(UUID.class);
        assertThat(officeOccupancy[1]).isInstanceOf(Long.class);

    }

    @Test
    @DisplayName("Find CANCELLED and NO-SHOW count by specialty")
    void findCancelledAndNoShowCountBySpecialty() {
        Appointment noShowAppointment = Appointment.builder()
                .appointmentStatus(AppointmentStatus.NO_SHOW)
                .startAt(LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.of(15, 0)))
                .endAt(LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.of(15, 30)))
                .date(LocalDate.now().minusDays(2))
                .patient(patient)
                .office(office)
                .doctor(doctor)
                .appointmentType(appointmentType)
                .observation("No-show appointment")
                .build();

        testEntityManager.persist(noShowAppointment);
        testEntityManager.flush();

        List<AppointmentStatus> statuses = List.of(
                AppointmentStatus.CANCELLED,
                AppointmentStatus.NO_SHOW
        );

        List<Object[]> result = appointmentRepository.findCancelledAndNoShowCountBySpecialty(statuses);

        assertThat(result).isNotEmpty();

        Object[] psychologyData = result.stream()
                .filter(data -> data[0].equals(specialty.getId()))
                .findFirst()
                .orElse(null);

        assertThat(psychologyData).isNotNull();
        assertThat((Long) psychologyData[1]).isEqualTo(2);
        assertThat(psychologyData[0]).isInstanceOf(UUID.class);
        assertThat(psychologyData[1]).isInstanceOf(Long.class);
    }

    @Test
    @DisplayName("Find doctors ranked on completed appointments")
    void findDoctorRankingByCompletedAppointments() {

        List<Object[]> ranking = appointmentRepository.findDoctorRankingByCompletedAppointments(
                AppointmentStatus.COMPLETED
        );

        assertThat(ranking).isNotEmpty();
        assertThat(ranking.getFirst()[0]).isEqualTo(doctor.getId());
        assertThat((Long) ranking.getFirst()[1]).isEqualTo(1);

    }

    @Test
    @DisplayName("Find appointments where status is NOT-SHOW")
    void findNoShowCountByPatientAndDateRange() {
        Appointment noShow1 = Appointment.builder()
                .appointmentStatus(AppointmentStatus.NO_SHOW)
                .startAt(LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.of(15, 0)))
                .endAt(LocalDateTime.of(LocalDate.now().minusDays(2), LocalTime.of(15, 30)))
                .date(LocalDate.now().minusDays(2))
                .patient(patient)
                .office(office)
                .doctor(doctor)
                .appointmentType(appointmentType)
                .observation("First no-show")
                .build();

        Appointment noShow2 = Appointment.builder()
                .appointmentStatus(AppointmentStatus.NO_SHOW)
                .startAt(LocalDateTime.of(LocalDate.now().minusDays(4), LocalTime.of(16, 0)))
                .endAt(LocalDateTime.of(LocalDate.now().minusDays(4), LocalTime.of(16, 30)))
                .date(LocalDate.now().minusDays(4))
                .patient(patient)
                .office(office)
                .doctor(doctor)
                .appointmentType(appointmentType)
                .observation("Second no-show")
                .build();

        testEntityManager.persist(noShow1);
        testEntityManager.persist(noShow2);
        testEntityManager.flush();


        LocalDateTime start = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime end   = LocalDate.now().plusDays(3).atTime(LocalTime.MAX);

        List<Object[]> noShows = appointmentRepository.findNoShowCountByPatientAndDateRange(
                AppointmentStatus.NO_SHOW, start, end
        );

        assertThat(noShows).isNotEmpty();

        Object[] patientNoShowData = noShows.stream()
                .filter(data -> data[0].equals(patient.getId()))
                .findFirst()
                .orElse(null);

        assertThat(patientNoShowData).isNotNull();
        assertThat((Long) patientNoShowData[2]).isGreaterThanOrEqualTo(2);
        assertThat(patientNoShowData[0]).isInstanceOf(UUID.class);
        assertThat(patientNoShowData[1]).isInstanceOf(String.class);
        assertThat(patientNoShowData[2]).isInstanceOf(Long.class);
    }

    @Test
    @DisplayName("Find appointments that are overlapping by doctor")
    void findOverlappingForDoctor() {

        boolean overlapping = appointmentRepository.existsOverlappingForDoctor(
                doctor.getId(),
                LocalDate.now(),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10,15)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10,45)),
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.COMPLETED)
        );

        assertThat(overlapping).isTrue();

        boolean notOverlapping = appointmentRepository.existsOverlappingForDoctor(
                doctor.getId(),
                LocalDate.now(),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(15,0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(15,30)),
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.COMPLETED)
        );
        assertThat(notOverlapping).isFalse();

    }

    @Test
    @DisplayName("Find appointments that are overlapping by Patient")
    void findOverlappingForPatient() {

        boolean overlapping = appointmentRepository.existsOverlappingForPatient(
                patient.getId(),
                LocalDate.now(),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10,15)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10,45)),
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.COMPLETED)
        );

        assertThat(overlapping).isTrue();

        boolean notOverlapping = appointmentRepository.existsOverlappingForDoctor(
                patient.getId(),
                LocalDate.now(),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(15,0)),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(15,30)),
                List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.COMPLETED)
        );

        assertThat(notOverlapping).isFalse();

    }

}