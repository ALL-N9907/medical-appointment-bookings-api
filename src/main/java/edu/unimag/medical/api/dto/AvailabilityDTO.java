package edu.unimag.medical.api.dto;

import java.io.Serializable;
import java.time.LocalTime;

public class AvailabilityDTO {

    public record AvailabilitySlotResponse(LocalTime startAt, LocalTime endAt) implements Serializable{}

}
