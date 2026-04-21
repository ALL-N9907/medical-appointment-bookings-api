package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.AppointmentDTOs.*;
import edu.unimag.medical.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> createAppointment(
            @Valid @RequestBody CreateAppointmentRequest req,
            UriComponentsBuilder uriBuilder
    ){
        try {
            var created = appointmentService.createAppointment(req);
            var location = uriBuilder.path("/api/appointments/{id}").buildAndExpand(created.id()).toUri();
            return ResponseEntity.created(location).body(created);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Schedule conflict")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> findById(@PathVariable UUID id){
        try {
            return ResponseEntity.ok(appointmentService.findByid(id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> findAll(){
        return ResponseEntity.ok(appointmentService.findAll());
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirmAppointment(@PathVariable UUID id){
        try {
            return ResponseEntity.ok(appointmentService.confirmAppointment(id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@Valid @RequestBody CancelAppointmentRequest req,
                                                                 @PathVariable UUID id){
        try {
            return ResponseEntity.ok(appointmentService.cancelAppointment(id, req));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(@Valid @RequestBody CompleteAppointmentRequest req,
                                                                   @PathVariable UUID id){
        try {
            return ResponseEntity.ok(appointmentService.completeAppointment(id, req));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @PutMapping("/{id}/no-show")
    public ResponseEntity<AppointmentResponse> setAsNoShowAppointment(@PathVariable UUID id){
        try {
            return ResponseEntity.ok(appointmentService.setAsNoShowAppointment(id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
}
