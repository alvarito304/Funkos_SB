package dev.alvaroherrero.funkosb.funko.controller;

import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.mapper.FunkoMapper;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.funko.service.IFunkoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/funkos")
@Slf4j
public class FunkoRestController {

    private IFunkoService service;

    @Autowired
    public FunkoRestController(IFunkoService funkoService) {
        this.service = funkoService;
    }

    @GetMapping
    public ResponseEntity<List<FunkoDTO>> getAllFunkos() {
        return ResponseEntity.ok(service.getFunkos());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<FunkoDTO>> getFunkosByName(@PathVariable String name) {
        return ResponseEntity.ok(service.getFunkosByName(name));
    }
    @GetMapping("/{id}")
    public ResponseEntity<FunkoDTO> getFunkoById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getFunkoById(id));
    }

    /**
     * Creates a new Funko.
     *
     * @param funko The Funko to create.
     * @return The created Funko.
     */
    @PostMapping
    public ResponseEntity<FunkoDTO> createFunko( @RequestBody Funko funko) {
        return ResponseEntity.ok(service.createFunko(funko));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunkoDTO> updateFunko(@Valid @PathVariable Long id, @RequestBody Funko funko) {
        return ResponseEntity.ok(service.updateFunko(id, funko));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FunkoDTO> deleteFunko(@PathVariable Long id) {
        return ResponseEntity.ok(service.deleteFunko(id));
    }

    @PatchMapping(value = "/imagen/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FunkoDTO> nuevoProducto(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        log.info("Actualizando imagen de producto por id: " + id);

        // Buscamos la raqueta
        if (!file.isEmpty()) {
            // Actualizamos el producto
            return ResponseEntity.ok(service.updateImage(id, file));

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado una imagen para el producto o esta está vacía");
        }
    }
}

