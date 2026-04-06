package edu.unimag.medical.domain.repository;

import edu.unimag.medical.domain.entities.Doctor;
import edu.unimag.medical.domain.entities.DoctorSchedule;
import edu.unimag.medical.domain.entities.Specialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DoctorScheduleRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private DoctorScheduleRepository doctorScheduleRepository;

    @Autowired
    TestEntityManager testEntityManager;


    Doctor doctor1;
    Doctor doctor2;

    @BeforeEach
    void setUp() {

        Specialty specialty = Specialty.builder()
                .name("General Medicine")
                .build();

        testEntityManager.persist(specialty);

        doctor1 = Doctor.builder()
                .fullName("Dr. Sebastian")
                .active(true)
                .specialty(specialty)
                .build();

        doctor2 = Doctor.builder()
                .fullName("Dr. Stone")
                .active(true)
                .specialty(specialty)
                .build();

        testEntityManager.persist(doctor1);
        testEntityManager.persist(doctor2);

        DoctorSchedule schedule1 = DoctorSchedule.builder()
                .doctor(doctor1)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startAt(LocalTime.of(9,0))
                .endAt(LocalTime.of(15,0))
                .build();

        DoctorSchedule schedule2 = DoctorSchedule.builder()
                .doctor(doctor1)
                .dayOfWeek(DayOfWeek.WEDNESDAY)
                .startAt(LocalTime.of(10,0))
                .endAt(LocalTime.of(18,0))
                .build();


        DoctorSchedule schedule3 = DoctorSchedule.builder()
                .doctor(doctor2)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startAt(LocalTime.of(10,0))
                .endAt(LocalTime.of(18,0))
                .build();

        testEntityManager.persist(schedule1);
        testEntityManager.persist(schedule2);
        testEntityManager.persist(schedule3);

        testEntityManager.flush();

    }

    @Test
    @DisplayName("Find doctor id and day of the week")
    void findByDoctor_IdAndDayOfWeek() {
        List<DoctorSchedule> schedules = doctorScheduleRepository.findByDoctor_IdAndDayOfWeek(
                doctor1.getId(), DayOfWeek.WEDNESDAY
        );

        assertThat(schedules).hasSize(1);
        assertThat(schedules.getFirst().getDoctor().getFullName()).isEqualTo("Dr. Sebastian");
    }

    @Test
    @DisplayName("Find by day of the week")
    void findByDayOfWeek() {
        List<DoctorSchedule> mondaySchedules = doctorScheduleRepository.findByDayOfWeek(DayOfWeek.MONDAY);
        assertThat(mondaySchedules).hasSize(2);
        assertThat(mondaySchedules).extracting(s -> s.getDoctor().getFullName())
                .containsExactlyInAnyOrder("Dr. Sebastian", "Dr. Stone");
    }

    @Test
    @DisplayName("count schedules by doctor")
    void countSchedulesByDoctor() {
        Long count = doctorScheduleRepository.countSchedulesByDoctor(doctor1.getId());
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("count doctors by day")
    void countDistinctDoctorsByDay() {
        Long count =doctorScheduleRepository.countDistinctDoctorsByDay(DayOfWeek.WEDNESDAY);
        assertThat(count).isEqualTo(1);
    }
}