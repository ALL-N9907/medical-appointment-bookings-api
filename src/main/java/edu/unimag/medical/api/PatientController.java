package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.PatientDTOs.*;
import edu.unimag.medical.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Validated
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponse> create(
            @Valid @RequestBody CreatePatientRequest req,
            UriComponentsBuilder uriBuilder){
        var patientCreated = patientService.create(req);
        var location = uriBuilder.path("/api/patients/{id}").buildAndExpand(patientCreated.id()).toUri();
        return ResponseEntity.created(location).body(patientCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> findById(@PathVariable UUID id){
        try {
            return ResponseEntity.ok(patientService.findById(id));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Patient not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<PatientResponse>> findAll() { return ResponseEntity.ok(patientService.findAll()); }


    @PutMapping("/{id}")
    public ResponseEntity<PatientResponse> updatePatient(
            @Valid @RequestBody UpdatePatientRequest req,
            @PathVariable UUID id){
        try {
            return ResponseEntity.ok(patientService.update(id, req));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Patient not found")) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

}
