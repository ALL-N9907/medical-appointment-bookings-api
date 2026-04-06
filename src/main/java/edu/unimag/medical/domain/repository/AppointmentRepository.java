package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Appointment;
import edu.unimag.medical.domain.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByPatient_IdAndAppointmentStatus(UUID patientId, AppointmentStatus status);

    List<Appointment> findByDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT a FROM Appointment a WHERE a.date BETWEEN :start AND :end")
    List<Appointment> findByRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT a FROM Appointment a WHERE a.office.id = :officeId " +
            "AND a.date = :date " +
            "AND a.startAt < :endAt " +
            "AND a.endAt > :startAt")
    List<Appointment> findOverlappingByOffice(
            @Param("officeId") UUID officeId,
            @Param("date") LocalDate date,
            @Param("startAt") LocalTime startAt,
            @Param("endAt") LocalTime endAt
    );

    List<Appointment> findByDoctor_IdAndDate(UUID doctorId, LocalDate date);

    @Query("SELECT a.office.id, COUNT(a) FROM Appointment a " +
            "WHERE a.date BETWEEN :from AND :to " +
            "GROUP BY a.office.id")
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

    @Query("SELECT a.doctor.id, COUNT(a) FROM Appointment a " +
            "WHERE a.appointmentStatus = :status " +
            "GROUP BY a.doctor.id " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> findDoctorRankingByCompletedAppointments(
            @Param("status") AppointmentStatus status
    );

    @Query("SELECT a.patient.id, COUNT(a) FROM Appointment a " +
            "WHERE a.appointmentStatus = :status " +
            "AND a.date BETWEEN :from AND :to " +
            "GROUP BY a.patient.id " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> findNoShowCountByPatientAndDateRange(
            @Param("status") AppointmentStatus status,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
