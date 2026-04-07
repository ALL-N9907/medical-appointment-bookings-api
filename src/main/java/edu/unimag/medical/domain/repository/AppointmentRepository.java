package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Appointment;
import edu.unimag.medical.domain.enums.AppointmentStatus;
import edu.unimag.medical.domain.enums.OfficeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByPatient_IdAndAppointmentStatus(UUID patientId, AppointmentStatus status);

    List<Appointment> findByDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.date = :date " +
            "AND a.startAt < :endAt " +
            "AND a.endAt > :startAt")
    List<Appointment> findByRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    List<Appointment> findByDoctor_IdAndDate(UUID doctorId, LocalDate date, List<AppointmentStatus> statuses);

    @Query("SELECT a.office.id, a.office.location, COUNT(a) FROM Appointment a " +
            "WHERE a.date BETWEEN :from AND :to " +
            "GROUP BY a.office.id, a.office.location")
    List<Object[]> findOfficeOccupancyByDateRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("SELECT a.doctor.specialty.id, COUNT(a) FROM Appointment a " +
            "WHERE a.appointmentStatus IN :statuses " +
            "GROUP BY a.doctor.specialty.id")
    List<Object[]> findCancelledAndNoShowCountBySpecialty(
            @Param("statuses") List<AppointmentStatus> statuses
    );

    @Query("SELECT a.doctor.id, a.doctor.fullName, COUNT(a) FROM Appointment a " +
            "WHERE a.appointmentStatus = :status " +
            "GROUP BY a.doctor.id, a.doctor.fullName " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> findDoctorRankingByCompletedAppointments(
            @Param("status") AppointmentStatus status
    );

    @Query("SELECT a.patient.id, a.patient.fullName, COUNT(a) FROM Appointment a " +
            "WHERE a.appointmentStatus = :status " +
            "AND a.date BETWEEN :from AND :to " +
            "GROUP BY a.patient.id, a.patient.fullName " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> findNoShowCountByPatientAndDateRange(
            @Param("status") AppointmentStatus status,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    @Query("""
    SELECT COUNT(a) > 0 FROM Appointment a
    WHERE a.doctor.id = :doctorId
    AND a.date = :date
    AND a.appointmentStatus IN :statuses
    AND a.startAt < :endAt
    AND a.endAt > :startAt
    """)
    boolean existsOverlappingForDoctor(@Param("doctorId") UUID doctorId,
                                       @Param("date") LocalDate date,
                                       @Param("startAt") LocalDateTime startAt,
                                       @Param("endAt") LocalDateTime endAt,
                                       @Param("statuses") List<AppointmentStatus> statuses);


    @Query("""
    SELECT COUNT(a) > 0 FROM Appointment a
    WHERE a.office.id = :officeId
    AND a.date = :date
    AND a.appointmentStatus IN :statuses
    AND a.startAt < :endAt
    AND a.endAt > :startAt
    """)
    boolean existsOverlappingForOffice(@Param("officeId") UUID officeId,
                                       @Param("date") LocalDate date,
                                       @Param("startAt") LocalDateTime startAt,
                                       @Param("endAt") LocalDateTime endAt,
                                       @Param("statuses") List<AppointmentStatus> statuses);



    @Query("""
    SELECT COUNT(a) > 0 FROM Appointment a
    WHERE a.patient.id = :patientId
    AND a.date = :date
    AND a.appointmentStatus IN :statuses
    AND a.startAt < :endAt
    AND a.endAt > :startAt
    """)
    boolean existsOverlappingForPatient(@Param("patientId") UUID patientId,
                                        @Param("date") LocalDate date,
                                        @Param("startAt") LocalDateTime startAt,
                                        @Param("endAt") LocalDateTime endAt,
                                        @Param("statuses") List<AppointmentStatus> statuses);
}