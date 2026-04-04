package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    List<Doctor> findBySpecialty_IdAndActive(UUID specialtyId);

}
