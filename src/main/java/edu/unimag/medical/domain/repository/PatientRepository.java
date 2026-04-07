package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Patient;
import edu.unimag.medical.domain.enums.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByEmail(String email);

    List<Patient> findByPatientStatusAndFullNameContainingIgnoreCase(PatientStatus patientStatus, String fullName);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.patientStatus = :status")
    long countByStatus(@Param("status") PatientStatus status);

    @Query("SELECT COUNT(p) > 0 FROM Patient p WHERE p.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT COUNT(p) > 0 FROM Patient p WHERE p.phone = :phone")
    boolean existsByPhone(@Param("phone") String phone);
}
