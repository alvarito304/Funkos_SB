package dev.alvaroherrero.funkosb.category.storage.service;

import dev.alvaroherrero.funkosb.category.dto.CategoryDTO;
import dev.alvaroherrero.funkosb.category.model.Category;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface ICategoryStorageService {
    void init();

    String store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void delete(String filename);

    List<CategoryDTO> readJson(MultipartFile file);

    void  createDefaultJson();

    void deleteAll();

    String getUrl(String filename);
}
