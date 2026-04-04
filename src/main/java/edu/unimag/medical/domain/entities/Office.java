package edu.unimag.medical.domain.entities;

import edu.unimag.medical.domain.enums.OfficeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "offices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Office {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "office_number", nullable = false)
    private Integer number;

    @Column(name = "location", nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "office_status" , nullable = false)
    private OfficeStatus officeStatus;

    @OneToMany(mappedBy = "office", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

}
