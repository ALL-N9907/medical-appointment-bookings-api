package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.DoctorDTOs.*;
import edu.unimag.medical.service.DoctorService;
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
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorResponse> create(
            @Valid @RequestBody CreateDoctorRequest req,
            UriComponentsBuilder uriBuilder){
        var doctorCreated = doctorService.create(req);
        var location = uriBuilder.path("/api/doctors/{id}").buildAndExpand(doctorCreated.id()).toUri();
        return ResponseEntity.created(location).body(doctorCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> findById(@PathVariable UUID id){
        return ResponseEntity.ok(doctorService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<DoctorResponse>> findAll(){
        return ResponseEntity.ok(doctorService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> updateDoctor(
            @Valid @RequestBody UpdateDoctorRequest req,
            @PathVariable UUID id){
        return ResponseEntity.ok(doctorService.update(id, req));
    }

}
