package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Appointment;
import edu.unimag.medical.domain.entities.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OfficeRepository extends JpaRepository<Office , UUID> {

}
