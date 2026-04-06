package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Office;
import edu.unimag.medical.domain.enums.OfficeStatus;
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
class OfficeRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private Office office1;
    private Office office2;
    private Office office3;
    private Office office4;

    @BeforeEach
    void setUp() {
        office1 = Office.builder()
                .number(101)
                .location("Norte")
                .officeStatus(OfficeStatus.AVAILABLE)
                .build();

        office2 = Office.builder()
                .number(102)
                .location("Norte")
                .officeStatus(OfficeStatus.AVAILABLE)
                .build();

        office3 = Office.builder()
                .number(103)
                .location("Sur")
                .officeStatus(OfficeStatus.UNAVAILABLE)
                .build();

        office4 = Office.builder()
                .number(104)
                .location("Este")
                .officeStatus(OfficeStatus.INACTIVE)
                .build();

        testEntityManager.persist(office1);
        testEntityManager.persist(office2);
        testEntityManager.persist(office3);
        testEntityManager.persist(office4);
        testEntityManager.flush();

    }

    @Test
    @DisplayName("Find by number")
    void findByNumber() {
        Optional<Office> found = officeRepository.findByNumber(office1.getNumber());

        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getLocation()).isEqualTo(office1.getLocation());
        assertThat(found.get().getOfficeStatus()).isEqualTo(office1.getOfficeStatus());
    }

    @Test
    @DisplayName("Find By status and location -- ignore Case")
    void findByOfficeStatusAndLocationContainingIgnoreCase() {
        List<Office> availableOffice = officeRepository.findByOfficeStatusAndLocationContainingIgnoreCase(
                OfficeStatus.AVAILABLE, "Norte"
        );
        assertThat(availableOffice.size()).isEqualTo(2);
        assertThat(availableOffice).extracting(Office::getNumber)
                .containsExactly(office1.getNumber(), office2.getNumber());
    }

    @Test
    @DisplayName("Count by Status")
    void countByStatus() {
        long availableCount = officeRepository.countByStatus(OfficeStatus.AVAILABLE);
        long unavailableCount = officeRepository.countByStatus(OfficeStatus.UNAVAILABLE);

        assertThat(availableCount).isEqualTo(2);
        assertThat(unavailableCount).isEqualTo(1);

    }

    @Test
    @DisplayName("If Office number exists")
    void existsByNumber() {
        boolean exists = officeRepository.existsByNumber(101);
        assertThat(exists).isTrue();

        boolean notExists = officeRepository.existsByNumber(169);
        assertThat(notExists).isFalse();
    }
}