package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

}
