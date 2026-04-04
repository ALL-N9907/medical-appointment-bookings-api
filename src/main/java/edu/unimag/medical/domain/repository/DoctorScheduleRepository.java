package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.DoctorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface DoctorScheduleRepository extends JpaRepository <DoctorSchedule, UUID> {

    List<DoctorSchedule> findByDoctor_IdAndDayOfWeek(UUID doctorId,DayOfWeek dayOfWeek);

}
