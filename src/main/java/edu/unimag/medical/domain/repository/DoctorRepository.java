package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    List<Doctor> findBySpecialty_IdAndActive(UUID specialtyId, boolean active);

    List<Doctor> findBySpecialty_NameIgnoreCaseAndActiveTrue(String specialtyName);

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.specialty.id = :specialtyId AND d.active = true")
    long countActiveBySpecialty(@Param("specialtyId") UUID specialtyId);

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.active = true")
    long countAllActiveDoctors();


}
