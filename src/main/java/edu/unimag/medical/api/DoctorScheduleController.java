package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.DoctorScheduleDTOs.*;
import edu.unimag.medical.service.DoctorScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Validated
public class DoctorScheduleController {

    private final DoctorScheduleService doctorScheduleService;

    @PostMapping("/{doctorId}/schedules")
    public ResponseEntity<DoctorScheduleResponse> createDoctorSchedule(
            @PathVariable UUID doctorId,
            @Valid @RequestBody CreateDoctorScheduleRequest req,
            UriComponentsBuilder uriBuilder
            ){
        var created = doctorScheduleService.createDoctorSchedule(doctorId, req);
        var location = uriBuilder.path("/api/doctors/{doctorId}/schedules/{scheduleId}").
                buildAndExpand(doctorId,created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{doctorId}/schedules")
    public ResponseEntity<List<DoctorScheduleResponse>> findAll(@PathVariable UUID doctorId){
        return ResponseEntity.ok(doctorScheduleService.findAll(doctorId));
    }

}
