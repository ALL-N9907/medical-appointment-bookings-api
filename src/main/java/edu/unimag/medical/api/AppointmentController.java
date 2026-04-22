package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.AppointmentDTOs.*;
import edu.unimag.medical.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest req,
            UriComponentsBuilder uriBuilder
    ){
        var created = appointmentService.createAppointment(req);
        var location = uriBuilder.path("/api/appointments/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> findById(@PathVariable UUID id){
        return ResponseEntity.ok(appointmentService.findByid(id));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> findAll(){
        return ResponseEntity.ok(appointmentService.findAll());
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirmAppointment(@PathVariable UUID id){
        return ResponseEntity.ok(appointmentService.confirmAppointment(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@Valid @RequestBody CancelAppointmentRequest req,
                                                                 @PathVariable UUID id){
        return ResponseEntity.ok(appointmentService.cancelAppointment(id, req));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(@Valid @RequestBody CompleteAppointmentRequest req,
                                                                   @PathVariable UUID id){
        return ResponseEntity.ok(appointmentService.completeAppointment(id, req));
    }

    @PutMapping("/{id}/no-show")
    public ResponseEntity<AppointmentResponse> setAsNoShowAppointment(@PathVariable UUID id){
        return ResponseEntity.ok(appointmentService.setAsNoShowAppointment(id));
    }
}
