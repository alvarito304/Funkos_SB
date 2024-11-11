package dev.alvaroherrero.funkosb.funko.controller;

import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.mapper.FunkoMapper;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.funko.service.IFunkoService;
import dev.alvaroherrero.funkosb.global.pageresponse.PageResponse;
import dev.alvaroherrero.funkosb.global.paginationlinkutils.PaginationLinksUtils;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funkos")
@Slf4j
public class FunkoRestController {

    private IFunkoService service;
    private FunkoMapper funkoMapper;
    private PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public FunkoRestController(IFunkoService funkoService, FunkoMapper funkoMapper, PaginationLinksUtils paginationLinksUtils) {
        this.paginationLinksUtils = paginationLinksUtils;
        this.funkoMapper = funkoMapper;
        this.service = funkoService;
    }

    @GetMapping()
    public ResponseEntity<PageResponse<FunkoDTO>> getAllFunkos(
            @RequestParam(required = false) Optional<String> name,
            @RequestParam(required = false) Optional<FunkoCategory> category,
            @RequestParam(required = false) Optional<Boolean> funkoSoftDeleted,
            @RequestParam(required = false) Optional<Double> price,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {

        log.info("Obteniendo funkos con las siguientes condiciones: " + category + " " + name + " " + price + " " + funkoSoftDeleted );

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos cómo va a ser la paginación
        Pageable pageable = PageRequest.of(page, size, sort);
        var funkos = service.getFunkos(name, category, funkoSoftDeleted, price, pageable);
        Page<FunkoDTO> funkoDTOs = funkos.map(funkoMapper::toDTO);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(funkoDTOs, uriBuilder))
                .body(PageResponse.of(funkoDTOs, sortBy, direction));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<FunkoDTO>> getFunkosByName(@PathVariable String name) {
        var funkos = service.getFunkosByName(name);
        var funkosDTOs = funkos.stream().map(funkoMapper::toDTO).toList();
        return ResponseEntity.ok(funkosDTOs);
    }
    @GetMapping("/{id}")
    public ResponseEntity<FunkoDTO> getFunkoById(@PathVariable Long id) {
        return ResponseEntity.ok(funkoMapper.toDTO(service.getFunkoById(id)));
    }

    /**
     * Creates a new Funko.
     *
     * @param funkoDTO The Funko to create.
     * @return The created Funko.
     */
    @PostMapping
    public ResponseEntity<FunkoDTO> createFunko(@Valid @RequestBody FunkoDTO funkoDTO) {
        // Convertir el DTO a entidad y llamar al servicio
        Funko funko = funkoMapper.toEntity(funkoDTO);
        Funko savedFunko = service.createFunko(funko);

        // Convertir la entidad guardada de vuelta a DTO para retornarla
        return ResponseEntity.ok(funkoMapper.toDTO(savedFunko));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunkoDTO> updateFunko(@Valid @PathVariable Long id, @RequestBody Funko funko) {
        return ResponseEntity.ok(funkoMapper.toDTO(service.updateFunko(id, funko)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FunkoDTO> deleteFunko(@PathVariable Long id) {
        return ResponseEntity.ok(funkoMapper.toDTO(service.deleteFunko(id)));
    }

    @PatchMapping(value = "/imagen/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FunkoDTO> nuevoProducto(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        log.info("Actualizando imagen de producto por id: " + id);

        // Buscamos la raqueta
        if (!file.isEmpty()) {
            // Actualizamos el producto
            return ResponseEntity.ok(funkoMapper.toDTO(service.updateImage(id, file)));

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado una imagen para el producto o esta está vacía");
        }
    }
}

