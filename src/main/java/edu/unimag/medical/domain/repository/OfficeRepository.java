package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Appointment;
import edu.unimag.medical.domain.entities.Office;
import edu.unimag.medical.domain.enums.OfficeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfficeRepository extends JpaRepository<Office , UUID> {

    Optional<Office> findByNumber(Integer number);

    List<Office> findByOfficeStatusAndLocationContainingIgnoreCase(OfficeStatus officeStatus, String location);

    @Query("SELECT COUNT(o) FROM Office o WHERE o.officeStatus = :status")
    long countByStatus(@Param("status") OfficeStatus status);

    @Query("SELECT COUNT(o) > 0 FROM Office o WHERE o.number = :number")
    boolean existsByNumber(@Param("number") Integer number);

}
