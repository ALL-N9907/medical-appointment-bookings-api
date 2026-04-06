package edu.unimag.medical.api.dto;

import java.io.Serializable;
import java.util.UUID;

public class ReportDTOs {

    public record OfficeOccupancyResponse(UUID officeId, Integer officeNumber, Long appointmentCount) implements Serializable {}
    public record DoctorProductivityResponse(UUID doctorId, String doctorName, Long completedAppointmentCount) implements Serializable{}
    public record NoShowPatientResponse(UUID patientId, String patientName, Long noShowAppointmentCount) implements Serializable{}

}
