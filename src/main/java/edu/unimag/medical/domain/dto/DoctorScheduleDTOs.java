package edu.unimag.medical.domain.dto;


import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public class DoctorScheduleDTOs {
    public record CreateDoctorScheduleRequest(@NotNull LocalTime startAt, @NotNull LocalTime endAt, @NotNull  DayOfWeek dayOfWeek) implements Serializable {}
    public record DoctorScheduleResponse(UUID id, @NotNull LocalTime startAt, @NotNull LocalTime endAt, DayOfWeek dayOfWeek) implements Serializable{}
}
