package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Patient;
import edu.unimag.medical.domain.enums.PatientStatus;
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
class PatientRepositoryTest extends AbstractRepositoryIT {

    Patient patient1;
    Patient patient2;
    Patient patient3;

    @Autowired private PatientRepository patientRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() {

        patient1 = Patient.builder()
                .fullName("John Doe")
                .email("johndoe@email.com")
                .phone("1111111111")
                .patientStatus(PatientStatus.ACTIVE)
                .build();

        patient2 = Patient.builder()
                .fullName("John Doe")
                .email("smithjohn@email.com")
                .phone("2222222222")
                .patientStatus(PatientStatus.INACTIVE)
                .build();

        patient3 = Patient.builder()
                .fullName("Jane Doe")
                .email("janedoe@email.com")
                .phone("3333333333")
                .patientStatus(PatientStatus.ACTIVE)
                .build();

        testEntityManager.persist(patient1);
        testEntityManager.persist(patient2);
        testEntityManager.persist(patient3);
        testEntityManager.flush();
    }

    @Test
    @DisplayName("Find by email")
    void findByEmail() {
        Optional<Patient> found =  patientRepository.findByEmail("johndoe@email.com");

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getFullName()).isEqualTo("John Doe");
        assertThat(found.get().getEmail()).isEqualTo("johndoe@email.com");
    }

    @Test
    void findByPatientStatusAndFullNameContainingIgnoreCase() {
        List<Patient> result = patientRepository.findByPatientStatusAndFullNameContainingIgnoreCase(PatientStatus.ACTIVE, "John");
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getFullName()).isEqualTo("John Doe");
    }

    @Test
    void countByStatus() {
        Long activeCount = patientRepository.countByStatus(PatientStatus.ACTIVE);
        Long inactiveCount = patientRepository.countByStatus(PatientStatus.INACTIVE);

        assertThat(activeCount).isEqualTo(2);
        assertThat(inactiveCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Exist by email")
    void existsByEmail() {
        boolean exists = patientRepository.existsByEmail("johndoe@email.com");
        assertThat(exists).isTrue();
    }
}