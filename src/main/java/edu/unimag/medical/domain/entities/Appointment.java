package edu.unimag.medical.domain.entities;

import edu.unimag.medical.domain.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_status", nullable = false)
    private AppointmentStatus appointmentStatus;

    @Column(name = "start_at", nullable = false)
    private LocalTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalTime endAt;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "cancellation_reason", nullable = true)
    private String cancellationReason;

    @Column(name = "observation", nullable = true)
    private String observation;

    @Column(name = "created_at") private LocalDateTime createdAt;
    @Column(name = "updated_at") private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (appointmentStatus == null) {
            appointmentStatus = AppointmentStatus.SCHEDULED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false) private Patient patient;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", nullable = false) private Office office;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false) private Doctor doctor;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_type_id", nullable = false) private AppointmentType appointmentType;
}
