package com.nk0shey.springboot1;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import java.net.URI;

import java.util.List;

@RestController
@RequestMapping("api/v1/software-engineers")
public class SoftwareEngineerController {

    private final SoftwareEngineerService softwareEngineerService;

    public SoftwareEngineerController(SoftwareEngineerService softwareEngineerService) {
        this.softwareEngineerService = softwareEngineerService;
    }

    @GetMapping
    public List<SoftwareEngineer> getEngineers() {
        return softwareEngineerService.getAllSoftwareEngineers();
    }

    @GetMapping("{id}")
    public SoftwareEngineer getEngineerById(
            @PathVariable Integer id
    ) {
        return softwareEngineerService.getSoftwareEngineerById(id);
    }

    @PostMapping
    public ResponseEntity<SoftwareEngineer> addNewSoftwareEngineer(@Valid @RequestBody SoftwareEngineer softwareEngineer) {
        SoftwareEngineer saved = softwareEngineerService.insertSoftwareEngineer(softwareEngineer);
        return ResponseEntity.created(URI.create("/api/v1/software-engineers/" + saved.getId())).body(saved);
    }

}
