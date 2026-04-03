package edu.unimag.medical.domain.entities;


import jakarta.persistence.*;
import lombok.*;


import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "doctor_schedules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "start_at", nullable = false)
    private LocalTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_week" , nullable = false)
    private DayOfWeek dayWeek;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false) private Doctor doctor;

}
