package dev.alvaroherrero.funkosb.category.storage.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alvaroherrero.funkosb.category.dto.CategoryDTO;
import dev.alvaroherrero.funkosb.category.mapper.CategoryMapper;
import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.category.service.ICategoryService;
import dev.alvaroherrero.funkosb.category.storage.controller.CategoryFileUploadController;
import dev.alvaroherrero.funkosb.category.storage.exceptions.StorageException;
import dev.alvaroherrero.funkosb.category.storage.exceptions.StorageFileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class CategoryStorageServiceImpl implements ICategoryStorageService {

    // Directorio raiz de nuestro almacén de ficheros
    private final Path rootLocation;
    private final ICategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryStorageServiceImpl(@Value("${upload-jsons.root-location}") String path, ICategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
        this.categoryService = categoryService;
        this.rootLocation = Paths.get(path);
    }


    /**
     * Método que almacena un fichero en el almacén secundario del
     * proyecto. Si el fichero ya existe, se reemplaza por el nuevo.
     * <p>
     * Si el fichero es vacío, se lanza una excepción de tipo
     * {@link StorageFileNotFoundException}. Si el nombre del fichero
     * contiene una ruta relativa fuera del directorio actual, se lanza
     * una excepción de tipo {@link StorageFileNotFoundException}.
     * <p>
     * Si se produce un error al almacenar el fichero, se lanza una
     * excepción de tipo {@link StorageException}.
     *
     * @param file Fichero a almacenar
     * @return nombre del fichero almacenado
     */
    @Override
    public String store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(filename);
        String justFilename = filename.replace("." + extension, "");
        String storedFilename = System.currentTimeMillis() + "_" + justFilename + "." + extension;

        try {
            if (file.isEmpty()) {
                throw new StorageFileNotFoundException("Fichero vacío " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageFileNotFoundException(
                        "No se puede almacenar un fichero con una ruta relativa fuera del directorio actual "
                                + filename);
            }
            if (!extension.equals("json")) {
                throw new StorageFileNotFoundException("El fichero debe ser un JSON " + filename);
            }

            try (InputStream inputStream = file.getInputStream()) {
                log.info("Almacenando fichero " + filename + " como " + storedFilename);
                Files.copy(inputStream, this.rootLocation.resolve(storedFilename),
                        StandardCopyOption.REPLACE_EXISTING);
                return storedFilename;
            }

        } catch (IOException e) {
            throw new StorageException("Fallo al almacenar fichero " + filename + " " + e);
        }

    }

    /**
     * Método que devuelve la ruta de todos los ficheros que hay
     * en el almacenamiento secundario del proyecto.
     */
    @Override
    public Stream<Path> loadAll() {
        log.info("Cargando todos los ficheros almacenados");
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("Fallo al leer ficheros almacenados " + e);
        }

    }

    /**
     * Método que es capaz de cargar un fichero a partir de su nombre
     * Devuelve un objeto de tipo Path
     */
    @Override
    public Path load(String filename) {
        log.info("Cargando fichero " + filename);
        return rootLocation.resolve(filename);
    }


    /**
     * Método que es capaz de cargar un fichero a partir de su nombre
     * Devuelve un objeto de tipo Resource
     */
    @Override
    public Resource loadAsResource(String filename) {
        log.info("Cargando fichero " + filename);
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("No se puede leer ficheroooo: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageException("Error al cargar fichero " + filename + " " + e);
        }
    }


    /**
     * Método que elimina todos los ficheros del almacenamiento
     * secundario del proyecto.
     */
    @Override
    public void deleteAll() {
        log.info("Eliminando todos los ficheros almacenados");
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }


    /**
     * Método que inicializa el almacenamiento secundario del proyecto
     */
    @Override
    public void init() {
        log.info("Inicializando almacenamiento");
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("No se puede inicializar el almacenamiento " + e);
        }
    }


    @Override
    public void delete(String filename) {
        String justFilename = StringUtils.getFilename(filename);
        try {
            log.info("Eliminando fichero " + filename);
            Path file = load(justFilename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new StorageException("No se puede eliminar el fichero " + filename + " " + e);
        }

    }

    @Override
    public List<CategoryDTO> readJson(MultipartFile filename) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Convierte el JSON del archivo a una lista de objetos Category
            var categorias =  mapper.readValue(
                    filename.getInputStream(),
                    new TypeReference<List<Category>>() {}
            );
            for (var category : categorias) {
                // Valida y maneja los datos de las categorías
                // Por ejemplo, comprueba que el nombre no está vacío
                if (!StringUtils.isEmpty(category.getCategory())) {
                   categoryService.createCategory(category);
                }
            }
            return categorias.stream().map(categoryMapper::toDTO).toList();
        } catch (IOException e) {
            // Maneja la excepción (por ejemplo, imprime el error o lanza una excepción personalizada)
            e.printStackTrace();
            //cambiar excepcion
            throw new StorageFileNotFoundException("Error al leer el JSON " + e); // o lanza una excepción personalizada
        }
    }

    @Override
    public void createDefaultJson() {
        ObjectMapper mapper = new ObjectMapper();
        String filename = "default.json";
        Path filePath = rootLocation.resolve(filename);

        var categories = categoryService.getAllCategories();

        var categoriesDTO = categories.stream().map(categoryMapper::toDTO).toList();


        try {
            // Serializar las categorías a JSON y guardarlas en el archivo
            mapper.writeValue(filePath.toFile(), categoriesDTO);
            log.info("Archivo default.json creado en " + filePath.toString());
        } catch (IOException e) {
            log.error("Error al crear default.json: " + e.getMessage());
            throw new StorageException("No se pudo crear el archivo default.json", e);
        }
    }

    /**
     * Método que devuelve la URL de un fichero a partir de su nombre
     * Devuelve un objeto de tipo String
     */
    @Override
    public String getUrl(String filename) {
        log.info("Obteniendo URL del fichero " + filename);
        return MvcUriComponentsBuilder
                // El segundo argumento es necesario solo cuando queremos obtener la imagen
                // En este caso tan solo necesitamos obtener la URL
                .fromMethodName(CategoryFileUploadController.class, "serveFile", filename, null)
                .build().toUriString();
    }



}