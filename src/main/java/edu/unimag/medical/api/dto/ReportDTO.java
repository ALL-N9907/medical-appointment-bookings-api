package edu.unimag.medical.api.dto;

import java.io.Serializable;
import java.util.UUID;

public class ReportDTO {

    public record OfficeOccupancyResponse(UUID officeId, String officeName, Long appointmentCount) implements Serializable {}
    public record DoctorProductivityResponse(UUID doctorId, String doctorName, Long completedAppointmentCount) implements Serializable{}
    public record NoShowPatientResponse(UUID patientId, String patientName, Long noShowAppointmentCount) implements Serializable{}

}
