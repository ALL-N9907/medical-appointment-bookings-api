package edu.unimag.medical.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "appointment_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AppointmentType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @OneToMany(mappedBy = "appointmentType", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

}
