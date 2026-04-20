package edu.unimag.medical.api;


import edu.unimag.medical.api.dto.SpecialtyDTOs.*;
import edu.unimag.medical.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
@Validated
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    public ResponseEntity<SpecialtyResponse> create(@Valid @RequestBody CreateSpecialtyRequest req,
                                                             UriComponentsBuilder uriBuilder){
        var specialtyCreated = specialtyService.create(req);
        var location = uriBuilder.path("/api/specialties/{id}").buildAndExpand(specialtyCreated.id()).toUri();
        return ResponseEntity.created(location).body(specialtyCreated);
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyResponse>> findAll(){

        return ResponseEntity.ok(specialtyService.findAll());
    }
}
