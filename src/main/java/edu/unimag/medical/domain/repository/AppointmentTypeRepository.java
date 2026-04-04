package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, UUID> {
}
