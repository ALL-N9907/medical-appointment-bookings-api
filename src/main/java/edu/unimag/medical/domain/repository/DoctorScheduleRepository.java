package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface DoctorScheduleRepository extends JpaRepository <DoctorSchedule, UUID> {

    List<DoctorSchedule> findByDoctor_IdAndDayWeek(UUID doctorId, DayOfWeek dayOfWeek);

    List<DoctorSchedule> findByDayWeek(DayOfWeek dayWeek);

    @Query("SELECT COUNT(ds) FROM DoctorSchedule ds WHERE ds.doctor.id = :doctorId")
    long countSchedulesByDoctor(@Param("doctorId") UUID doctorId);

    @Query("SELECT COUNT(DISTINCT ds.doctor.id) FROM DoctorSchedule ds WHERE ds.dayWeek = :day")
    long countDistinctDoctorsByDay(@Param("day") DayOfWeek day);


}
