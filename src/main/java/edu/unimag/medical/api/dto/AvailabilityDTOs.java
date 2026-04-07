package edu.unimag.medical.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;


public class AvailabilityDTOs {

    public record AvailabilitySlotResponse(LocalDateTime startAt, LocalDateTime endAt) implements Serializable{}

}
