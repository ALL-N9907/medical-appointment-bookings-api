package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Doctor;
import edu.unimag.medical.domain.entities.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;

//import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DoctorRepositoryTest extends AbstractRepositoryIT {
    
    @Autowired private DoctorRepository doctorRepository;

    @Autowired TestEntityManager testEntityManager;

    private Specialty nutrition;
    private Specialty physiotherapy;

    @BeforeEach
    void setUp() {

        physiotherapy = Specialty.builder()
                .name("Physiotherapy").build();

        nutrition = Specialty.builder()
                .name("Nutrition").build();

        testEntityManager.persist(physiotherapy);
        testEntityManager.persist(nutrition);

        Doctor doctor1 = Doctor.builder()
                .fullName("Dr. Chapatin")
                .active(true)
                .specialty(nutrition)
                .build();

        Doctor doctor2 = Doctor.builder()
                .fullName("Dr. House")
                .active(true)
                .specialty(physiotherapy)
                .build();

        Doctor doctor3 = Doctor.builder()
                .fullName("Dr. Robert")
                .active(false)
                .specialty(nutrition)
                .build();

        testEntityManager.persist(doctor1);
        testEntityManager.persist(doctor2);
        testEntityManager.persist(doctor3);

        testEntityManager.flush();



    }

    @Test
    @DisplayName("Find doctors by especialty id ad active status")
    void findBySpecialty_IdAndActive() {
        List<Doctor> activeNutritionists =
                doctorRepository.findBySpecialty_IdAndActive(nutrition.getId(), true);

        assertThat(activeNutritionists).hasSize(1);
        assertThat(activeNutritionists.getFirst().getFullName()).isEqualTo("Dr. Chapatin");

    }

    @Test
    @DisplayName("Find doctors by specialty name -- ignoring case and active true")
    void findBySpecialty_NameIgnoreCaseAndActiveTrue() {
        List<Doctor> physiotherapists =
                doctorRepository.findBySpecialty_NameIgnoreCaseAndActiveTrue("physiotherapy");

        assertThat(physiotherapists).hasSize(1);
        assertThat(physiotherapists.getFirst().getFullName()).isEqualTo("Dr. House");
    }

    @Test
    @DisplayName("Count active doctors by specialty")
    void countActiveBySpecialty() {
        long activeNutritionists = doctorRepository.countActiveBySpecialty(nutrition.getId());
        long activePhisyotherapists = doctorRepository.countActiveBySpecialty(physiotherapy.getId());

        assertThat(activeNutritionists).isEqualTo(1);
        assertThat(activePhisyotherapists).isEqualTo(1);
    }

    @Test
    @DisplayName("Count all active doctors")
    void countAllActiveDoctors() {
        Long activeDoctors = doctorRepository.countAllActiveDoctors();
        assertThat(activeDoctors).isEqualTo(2);
    }
}