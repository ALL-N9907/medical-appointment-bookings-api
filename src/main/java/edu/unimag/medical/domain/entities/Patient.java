package edu.unimag.medical.domain.entities;

import edu.unimag.medical.domain.enums.PatientStatus;
import jakarta.persistence.*;
import lombok.*;


import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "patient_status" , nullable = false)
    private PatientStatus patientStatus;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

}
