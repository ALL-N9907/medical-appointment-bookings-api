package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {

    Optional<Specialty> findByNameIgnoreCase(String name);

    @Query("SELECT DISTINCT d.specialty FROM Doctor d WHERE d.active = true")
    List<Specialty> findSpecialtiesWithActiveDoctors();

    @Query("SELECT COUNT(d) FROM Doctor d WHERE d.specialty.id = :id AND d.active = true")
    long countActiveDoctorsBySpecialty(@Param("id") UUID id);

    @Query("SELECT COUNT(s) > 0 FROM Specialty s WHERE LOWER(s.name) = LOWER(:name)")
    boolean existsByName(@Param("name") String name);

}
