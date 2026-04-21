package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.DoctorDTOs.*;
import edu.unimag.medical.service.DoctorService;
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
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Validated
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<? extends Object> create(
            @Valid @RequestBody CreateDoctorRequest req,
            UriComponentsBuilder uriBuilder){
        try {
            var doctorCreated = doctorService.create(req);
            var location = uriBuilder.path("/api/doctors/{id}").buildAndExpand(doctorCreated.id()).toUri();
            return ResponseEntity.created(location).body(doctorCreated);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> findById(@PathVariable UUID id){
        try {
            return ResponseEntity.ok(doctorService.findById(id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Doctor not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> findAll(){
        return ResponseEntity.ok(doctorService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<? extends Object> updateDoctor(
            @Valid @RequestBody UpdateDoctorRequest req,
            @PathVariable UUID id){
        try {
            return ResponseEntity.ok(doctorService.update(id, req));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Doctor not found")) {
                return ResponseEntity.notFound().build();
            }
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
            throw e;
        }
    }

}
