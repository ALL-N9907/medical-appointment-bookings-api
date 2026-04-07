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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class SpecialtyRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private Specialty generalMedicine;
    private Specialty psychology ;
    private Specialty physiotherapy;
    private Specialty nutrition;
    private Doctor doctor1;
    private Doctor doctor2;
    private Doctor doctor3;
    private Doctor doctor4;

    @BeforeEach
    void setUp() {

        generalMedicine = Specialty.builder()
                .name("General Medicine")
                .build();

        psychology   = Specialty.builder()
                .name("Psychology")
                .build();

        physiotherapy = Specialty.builder()
                .name("Physiotherapy")
                .build();

        nutrition = Specialty.builder()
                .name("Nutrition")
                .build();

        testEntityManager.persist(generalMedicine);
        testEntityManager.persist(psychology);
        testEntityManager.persist(physiotherapy);
        testEntityManager.persist(nutrition);

        doctor1 = Doctor.builder()
                .fullName("Dr. Martinez")
                .active(true)
                .specialty(generalMedicine)
                .build();

        doctor2 = Doctor.builder()
                .fullName("Dr. Jones")
                .active(true)
                .specialty(psychology)
                .build();

        doctor3 = Doctor.builder()
                .fullName("Dr. Pizarro")
                .active(true)
                .specialty(physiotherapy)
                .build();

        doctor4 = Doctor.builder()
                .fullName("Dr. Vargas")
                .active(false)
                .specialty(nutrition)
                .build();

        testEntityManager.persist(doctor1);
        testEntityManager.persist(doctor2);
        testEntityManager.persist(doctor3);
        testEntityManager.persist(doctor4);
        testEntityManager.flush();

    }

    @Test
    @DisplayName("Find by name ignore case")
    void findByNameIgnoreCase() {

        Optional<Specialty> found = specialtyRepository.findByNameIgnoreCase("general medicine");

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getName()).isEqualTo("General Medicine");
    }

    @Test
    @DisplayName("find Specialties with active doctors")
    void findSpecialtiesWithActiveDoctors() {
        List<Specialty> specialties = specialtyRepository.findSpecialtiesWithActiveDoctors();

        assertThat(specialties).isNotEmpty();

        boolean generalMedicineFound = specialties.stream()
                .anyMatch(s -> s.getName().equals("General Medicine"));
        assertThat(generalMedicineFound).isTrue();

        boolean psychologyFound = specialties.stream()
                .anyMatch(s -> s.getName().equals("Psychology"));
        assertThat(psychologyFound).isTrue();

        boolean physiotherapyFound = specialties.stream()
                .anyMatch(s -> s.getName().equals("Physiotherapy"));
        assertThat(physiotherapyFound).isTrue();

        boolean nutritionFound = specialties.stream()
                .anyMatch(s -> s.getName().equals("Nutrition"));
        assertThat(nutritionFound).isFalse();


    }

    @Test
    void countActiveDoctorsBySpecialty() {
        long generalMedicineCount = specialtyRepository.countActiveDoctorsBySpecialty(generalMedicine.getId());
        assertThat(generalMedicineCount).isEqualTo(1);

        long psychologyCount = specialtyRepository.countActiveDoctorsBySpecialty(psychology.getId());
        assertThat(psychologyCount).isEqualTo(1);

        long physiotherapyCount = specialtyRepository.countActiveDoctorsBySpecialty(physiotherapy.getId());
        assertThat(physiotherapyCount).isEqualTo(1);

        long nutritionCount = specialtyRepository.countActiveDoctorsBySpecialty(nutrition.getId());
        assertThat(nutritionCount).isEqualTo(0);


    }

    @Test
    @DisplayName("exist by name - ignore case")
    void existsByName() {

        boolean existsLowerCase = specialtyRepository.existsByName("general medicine");
        boolean existsUpperCase = specialtyRepository.existsByName("NUTRITION");
        assertThat(existsLowerCase).isTrue();
        assertThat(existsUpperCase).isTrue();

        boolean exists = specialtyRepository.existsByName("Cardiology");
        assertThat(exists).isFalse();

    }
}