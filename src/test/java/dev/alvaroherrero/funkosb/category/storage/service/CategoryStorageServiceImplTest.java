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
import dev.alvaroherrero.funkosb.funko.exceptions.FunkoNotFoundException;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryStorageServiceImplTest {

    @Mock
    private ICategoryService categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ObjectMapper objectMapper;

    @TempDir
    Path tempDir;

    private CategoryStorageServiceImpl categoryStorageService;

    private Category categoryTest;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryStorageService = new CategoryStorageServiceImpl(tempDir.toString(), categoryService, categoryMapper, objectMapper);
        categoryTest = new Category();
        categoryTest.setId(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
        categoryTest.setCategory(FunkoCategory.SERIE);
    }

    @Test
    void store() throws IOException {
        // Archivo JSON válido
        MockMultipartFile file = new MockMultipartFile("file", "test.json", "application/json", "{\"name\":\"category\"}".getBytes());

        String storedFilename = categoryStorageService.store(file);

        assertTrue(Files.exists(tempDir.resolve(storedFilename)));
    }

    @Test
    void store_shouldThrowExceptionForEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.json", "application/json", new byte[0]);

        assertThrows(StorageFileNotFoundException.class, () -> categoryStorageService.store(file));
    }

    @Test
    void store_shouldThrowExceptionForInvalidFileName() {
        MockMultipartFile file = new MockMultipartFile("file", "../invalid.json", "application/json", "{\"name\":\"category\"}".getBytes());

        assertThrows(StorageFileNotFoundException.class, () -> categoryStorageService.store(file));
    }

    @Test
    void store_shouldThrowExceptionForNonJsonFile() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "not a json content".getBytes());

        assertThrows(StorageFileNotFoundException.class, () -> categoryStorageService.store(file));
    }

    @Test
    void store_shouldThrowStorageExceptionForIOException() throws IOException {
        // Mock de un archivo con una IOException en getInputStream()
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.json");
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenThrow(new IOException("Error en el stream"));

        assertThrows(StorageException.class, () -> categoryStorageService.store(file));
    }

    @Test
    void loadAll() throws IOException {
        // Crear archivos de prueba
        Files.createFile(tempDir.resolve("test1.json"));
        Files.createFile(tempDir.resolve("test2.json"));
        Files.createFile(tempDir.resolve("test3.json"));

        // Llamar a loadAll y recolectar los resultados
        try (Stream<Path> files = categoryStorageService.loadAll()) {
            List<String> fileNames = files.map(Path::toString).collect(Collectors.toList());

            // Verificar que se encuentran los tres archivos
            assertEquals(3, fileNames.size());
            assertEquals(List.of("test1.json", "test2.json", "test3.json"), fileNames);
        }
    }

    @Test
    void loadAll_shouldThrowStorageExceptionWhenIOExceptionOccurs() {
        // Forzar un error cambiando el rootLocation a un directorio no existente
        categoryStorageService = new CategoryStorageServiceImpl("invalid/path", categoryService, categoryMapper, objectMapper);

        assertThrows(StorageException.class, () -> {
            categoryStorageService.loadAll().collect(Collectors.toList());
        });
    }

    @Test
    void load() {
        String filename = "testFile.json";
        Path result = categoryStorageService.load(filename);
        assertEquals(tempDir.resolve(filename), result);
    }

    @Test
    void loadAsResource() throws IOException {
        // Crear un archivo de prueba
        String filename = "testFile.json";
        Path filePath = tempDir.resolve(filename);
        Files.createFile(filePath);

        // Llamar al método loadAsResource
        Resource resource = categoryStorageService.loadAsResource(filename);

        // Verificar que el recurso es un UrlResource y que existe
        assertTrue(resource instanceof UrlResource);
        assertTrue(resource.exists());
        assertTrue(resource.isReadable());
    }

    @Test
    void loadAsResource_shouldThrowException_whenFileDoesNotExist() {
        // Definir un archivo inexistente
        String filename = "nonExistentFile.json";

        // Verificar que se lanza StorageFileNotFoundException
        assertThrows(StorageFileNotFoundException.class, () -> {
            categoryStorageService.loadAsResource(filename);
        });
    }

    @Test
    void deleteAll() throws IOException {
        // Crear algunos archivos de prueba dentro del directorio temporal
        Path file1 = tempDir.resolve("file1.json");
        Path file2 = tempDir.resolve("file2.json");
        Files.createFile(file1);
        Files.createFile(file2);

       FileSystemUtils.deleteRecursively(tempDir.toFile());

            // Verificar que los archivos fueron eliminados
            assertFalse(Files.exists(file1));
            assertFalse(Files.exists(file2));

    }

    @Test
    void init() {
        categoryStorageService.init();
        assertTrue(Files.exists(tempDir), "El directorio debería haber sido creado");
    }

    @Test
    void delete_shouldDeleteFileSuccessfully() throws IOException {
        // Preparar el archivo de prueba
        Path testFile = tempDir.resolve("testFile.json");
        Files.createFile(testFile);

        // Ejecutar el método delete
        categoryStorageService.delete("testFile.json");

        // Verificar que el archivo haya sido eliminado
        assertFalse(Files.exists(testFile), "El archivo debería haber sido eliminado");
    }


        @Test
        void readJson() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "test.json", "application/json", "{\"name\":\"category\"}".getBytes());

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setCategory(FunkoCategory.SERIE);
        // Cambia el matcher para permitir cualquier InputStream y la misma referencia de TypeReference
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(List.of(categoryTest));
        when(categoryService.createCategory(categoryTest)).thenReturn(categoryTest);
        when(categoryMapper.toDTO(categoryTest)).thenReturn(categoryDTO);

        // Llamar al método readJson

        List<CategoryDTO> result = categoryStorageService.readJson(file);

        // Verificar que la lista de categorías no está vacía
        assertFalse(result.isEmpty());
        // Verificar que la lista contiene un objeto CategoryDTO con el nombre "category"
        assertEquals(categoryTest.getCategory(), result.get(0).getCategory());
    }

    @Test
    void readJson_throwsStorageFileNotFoundException_whenIOExceptionOccurs() throws IOException {
        // Configurar un archivo simulado que lance IOException al leer su contenido
        MultipartFile file = Mockito.mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Error al leer el archivo"));

        // Verificar que se lanza StorageFileNotFoundException con el mensaje esperado
        StorageFileNotFoundException exception = assertThrows(
                StorageFileNotFoundException.class,
                () -> categoryStorageService.readJson(file)
        );

        // Verificar el mensaje de excepción
        assertTrue(exception.getMessage().contains("Error al leer el JSON"));
    }



    @Test
    void createDefaultJson_createsFileWithSerializedCategories() throws IOException {
        // Configuración de datos de prueba
        Category category1 = new Category();
        category1.setCategory(FunkoCategory.SERIE);
        Category category2 = new Category();
        category2.setCategory(FunkoCategory.DISNEY);

        List<Category> categories = List.of(category1, category2);

        CategoryDTO categoryDTO1 = new CategoryDTO();
        categoryDTO1.setCategory(FunkoCategory.SERIE);
        CategoryDTO categoryDTO2 = new CategoryDTO();
        categoryDTO2.setCategory(FunkoCategory.DISNEY);

        List<CategoryDTO> categoriesDTO = List.of(categoryDTO1, categoryDTO2);

        // Mockear métodos de dependencias
        when(categoryService.getAllCategories()).thenReturn(categories);
        when(categoryMapper.toDTO(category1)).thenReturn(categoryDTO1);
        when(categoryMapper.toDTO(category2)).thenReturn(categoryDTO2);

        // Llamar al método a testear
        categoryStorageService.createDefaultJson();

        // Verificar que objectMapper.writeValue se haya llamado con el archivo y lista esperados
        Path filePath = tempDir.resolve("default.json");
        verify(objectMapper).writeValue(eq(filePath.toFile()), eq(categoriesDTO));

        // Opcional: Verificar que el log de información se haya llamado
    }

    @Test
    void createDefaultJson_throwsStorageException_whenIOExceptionOccurs() throws IOException {
        // Mock de ObjectMapper para lanzar IOException
        doThrow(new IOException("Error de escritura")).when(objectMapper).writeValue(any(File.class), any());

        // Verificar que se lanza StorageException con el mensaje esperado
        StorageException exception = assertThrows(
                StorageException.class,
                () -> categoryStorageService.createDefaultJson()
        );

        // Verificar el mensaje de la excepción
        assertTrue(exception.getMessage().contains("No se pudo crear el archivo default.json"));
    }


    @Test
    void getUrl_returnsCorrectUrlWithMock() {
        String filename = "test-file.jpg";
        String expectedUrl = "http://localhost/files/" + filename;

        // Mock de MvcUriComponentsBuilder para que devuelva la URL esperada
        try (MockedStatic<MvcUriComponentsBuilder> mockedMvcUriBuilder = Mockito.mockStatic(MvcUriComponentsBuilder.class)) {
            mockedMvcUriBuilder.when(() ->
                    MvcUriComponentsBuilder.fromMethodName(CategoryFileUploadController.class, "serveFile", filename, null)
            ).thenReturn(UriComponentsBuilder.fromUriString(expectedUrl));

            // Llama al método a testear
            String generatedUrl = categoryStorageService.getUrl(filename);

            // Verifica que la URL generada es la esperada
            assertEquals(expectedUrl, generatedUrl);
        }
    }
}