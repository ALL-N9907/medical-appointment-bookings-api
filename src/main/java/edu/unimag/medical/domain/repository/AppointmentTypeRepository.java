package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, UUID> {

    boolean existsByName(String name);

    List<AppointmentType> findByDurationMinutesLessThanEqual(Integer maxMinutes);

    @Query("SELECT AVG(a.durationMinutes) FROM AppointmentType a")
    Double avgDurationMinutes();

    @Query("SELECT COUNT(a) FROM AppointmentType a WHERE a.durationMinutes = :minutes")
    long countByDuration(@Param("minutes") Integer minutes);
}
