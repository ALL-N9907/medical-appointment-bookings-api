package edu.unimag.medical.api.dto;

import edu.unimag.medical.domain.enums.AppointmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class AppointmentDTO {
    public record CreateAppointmentRequest(
            @NotNull UUID patientId, @NotNull UUID doctorId,
            @NotNull UUID officeId, @NotNull UUID appointmenttTypeId,
            @NotNull LocalTime startAt, @NotNull LocalDate date
            ) implements Serializable{}

    public record CancelAppointmentRequest(@NotBlank String cancellationReason) implements Serializable {}

    public record CompleteAppointmentRequest(String observations) implements Serializable{}

    public record AppointmentResponse(
            UUID id, PatientDTOs.PatientResponse patient, DoctorDTOs.DoctorResponse doctor,
            OfficeDTOs.OfficeResponse office, AppointmentTypeDTO.AppointmentTypeResponse appointmentType, LocalTime startAt, LocalTime endAt
            ,LocalDate date,AppointmentStatus status, String cancellationReason, String observations
            ) implements Serializable{}
}
