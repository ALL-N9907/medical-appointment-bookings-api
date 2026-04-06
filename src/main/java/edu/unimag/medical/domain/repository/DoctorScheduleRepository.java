package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorScheduleRepository extends JpaRepository <DoctorSchedule, UUID> {

    Optional<DoctorSchedule> findByDoctor_IdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);
    List<DoctorSchedule> findByDayOfWeek(DayOfWeek dayOfWeek);
    List<DoctorSchedule> findByDoctor_id(UUID doctorId);
    boolean existsByDoctor_IdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);

    @Query("SELECT COUNT(ds) FROM DoctorSchedule ds WHERE ds.doctor.id = :doctorId")
    long countSchedulesByDoctor(@Param("doctorId") UUID doctorId);

    @Query("SELECT COUNT(DISTINCT ds.doctor.id) FROM DoctorSchedule ds WHERE ds.dayWeek = :day")
    long countDistinctDoctorsByDay(@Param("day") DayOfWeek day);


}
