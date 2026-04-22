package edu.unimag.medical.api;

import edu.unimag.medical.api.dto.AppointmentTypeDTOs.*;
import edu.unimag.medical.service.AppointmentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/appointment-types")
@RequiredArgsConstructor
@Validated
public class AppointmentTypeController {

    private final AppointmentTypeService appointmentTypeService;

    @PostMapping
    public ResponseEntity<AppointmentTypeResponse> create(@Valid @RequestBody CreateAppointmentTypeRequest req, UriComponentsBuilder uriBuilder){

        var AppointmentTypeCreated = appointmentTypeService.create(req);
        var location = uriBuilder.path("/api/appointment-type/{id}").buildAndExpand(AppointmentTypeCreated.id()).toUri();
        return ResponseEntity.created(location).body(AppointmentTypeCreated);


    }

    @GetMapping
    public ResponseEntity<List<AppointmentTypeResponse>> findAll(){
        return ResponseEntity.ok(appointmentTypeService.findAll());
    }
}
