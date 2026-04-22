package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.OfficeDTOs.*;
import edu.unimag.medical.service.OfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/offices")
@RequiredArgsConstructor
@Validated
public class OfficeController {

    private final OfficeService officeService;

    @PostMapping
    public ResponseEntity<OfficeResponse> create(@Valid @RequestBody CreateOfficeRequest req,
                                                 UriComponentsBuilder uriBuilder){
        var officeCreated = officeService.create(req);
        var location = uriBuilder.path("/api/offices/{id}").buildAndExpand(officeCreated.id()).toUri();
        return ResponseEntity.created(location).body(officeCreated);
    }

    @GetMapping
    public ResponseEntity<List<OfficeResponse>> findAll(){

        return ResponseEntity.ok(officeService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfficeResponse> updateOffice(
            @Valid @RequestBody UpdateOfficeRequest req,
            @PathVariable UUID id){
        return ResponseEntity.ok(officeService.update(id, req));
    }
}
