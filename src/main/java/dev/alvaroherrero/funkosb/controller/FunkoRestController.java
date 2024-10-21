package dev.alvaroherrero.funkosb.controller;

import dev.alvaroherrero.funkosb.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.mapper.FunkoMapper;
import dev.alvaroherrero.funkosb.model.Funko;
import dev.alvaroherrero.funkosb.services.IFunkoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funkos")
public class FunkoRestController {

    private IFunkoService service;

    @Autowired
    public FunkoRestController(IFunkoService funkoService) {
        this.service = funkoService;
    }

    @GetMapping
    public ResponseEntity<List<FunkoDTO>> getAllFunkos() {
        List<Funko> funkos = service.getFunkos();
        List<FunkoDTO> dtos;
        dtos = funkos.stream().map(FunkoMapper::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FunkoDTO> getFunkoById(@PathVariable Long id) {
        Funko funko = service.getFunkoById(id);
        FunkoDTO dto = FunkoMapper.toDTO(funko);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<FunkoDTO> createFunko(@Valid @RequestBody FunkoDTO funko) {
        return ResponseEntity.ok(FunkoMapper.toDTO(service.createFunko(FunkoMapper.toEntity(funko))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunkoDTO> updateFunko( @Valid @PathVariable Long id, @RequestBody Funko funko) {
        return ResponseEntity.ok(FunkoMapper.toDTO(service.updateFunko(id, funko)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FunkoDTO> deleteFunko(@PathVariable Long id) {
        return ResponseEntity.ok(FunkoMapper.toDTO(service.deleteFunko(id)));
    }
}

