package dev.alvaroherrero.funkosb.category.storage.controller;

import dev.alvaroherrero.funkosb.category.dto.CategoryDTO;
import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.category.storage.service.ICategoryStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/category/files")
@Slf4j
public class CategoryFileUploadController {

    private final ICategoryStorageService storageService;

    @Autowired
    public CategoryFileUploadController(ICategoryStorageService storageService) {
        this.storageService = storageService;
    }

    @PutMapping( consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<CategoryDTO> uploadFile(@RequestPart("file") MultipartFile file) {

        log.info("Uploading file");
        storageService.store(file);
        // Buscamos la raqueta
        if (!file.isEmpty()) {
            var categorias = storageService.readJson(file);
            return categorias;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado un json para las categorias o esta está vacía");
        }
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Resource> serveDefaultFile() {
        // Supongamos que el JSON predeterminado se guarda como "default.json" en tu almacenamiento
        String defaultFilename = "default.json";
        storageService.createDefaultJson();
        Resource file = storageService.loadAsResource(defaultFilename);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"categories.json\"") // Forzar descarga con nombre 'categories.json'
                .body(file);
    }

}