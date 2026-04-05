package edu.unimag.medical.domain.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DoctorRepositoryTest {
    

    @BeforeEach
    void setUp() {

    }

    @Test
    void findBySpecialty_IdAndActive() {
    }

    @Test
    void findBySpecialty_NameIgnoreCaseAndActiveTrue() {
    }

    @Test
    void countActiveBySpecialty() {
    }

    @Test
    void countAllActiveDoctors() {
    }
}