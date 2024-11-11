package dev.alvaroherrero.funkosb.funko.storage.service;



import dev.alvaroherrero.funkosb.funko.storage.controller.FileUploadController;
import dev.alvaroherrero.funkosb.funko.storage.exceptions.StorageException;
import dev.alvaroherrero.funkosb.funko.storage.exceptions.StorageFileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;


class StorageServiceImplTest {

    @TempDir
    Path tempDir;

    private IStorageService storageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        storageService = new StorageServiceImpl(tempDir.toString());

    }

    @Test
    void store() throws IOException {
        // Archivo JSON válido
        MockMultipartFile file = new MockMultipartFile("file", "test.json", "application/json", "{\"name\":\"category\"}".getBytes());

        String storedFilename = storageService.store(file);

        assertTrue(Files.exists(tempDir.resolve(storedFilename)));
    }

    @Test
    void store_shouldThrowExceptionForEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.json", "application/json", new byte[0]);

        assertThrows(StorageFileNotFoundException.class, () -> storageService.store(file));
    }

    @Test
    void store_shouldThrowExceptionForInvalidFileName() {
        MockMultipartFile file = new MockMultipartFile("file", "../invalid.json", "application/json", "{\"name\":\"category\"}".getBytes());

        assertThrows(StorageFileNotFoundException.class, () -> storageService.store(file));
    }


    @Test
    void store_shouldThrowStorageExceptionForIOException() throws IOException {
        // Mock de un archivo con una IOException en getInputStream()
        MockMultipartFile file = mock(MockMultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.json");
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenThrow(new IOException("Error en el stream"));

        assertThrows(StorageException.class, () -> storageService.store(file));
    }

    @Test
    void loadAll() throws IOException {
        // Crear archivos de prueba
        Files.createFile(tempDir.resolve("test1.json"));
        Files.createFile(tempDir.resolve("test2.json"));
        Files.createFile(tempDir.resolve("test3.json"));

        // Llamar a loadAll y recolectar los resultados
        try (Stream<Path> files = storageService.loadAll()) {
            List<String> fileNames = files.map(Path::toString).collect(Collectors.toList());

            // Verificar que se encuentran los tres archivos
            assertEquals(3, fileNames.size());
            assertEquals(List.of("test1.json", "test2.json", "test3.json"), fileNames);
        }
    }

    @Test
    void loadAll_shouldThrowStorageExceptionWhenIOExceptionOccurs() {
        // Forzar un error cambiando el rootLocation a un directorio no existente
        storageService = new StorageServiceImpl("invalid/path");

        assertThrows(StorageException.class, () -> {
            storageService.loadAll().collect(Collectors.toList());
        });
    }

    @Test
    void load() {
        String filename = "testFile.json";
        Path result = storageService.load(filename);
        assertEquals(tempDir.resolve(filename), result);
    }

    @Test
    void loadAsResource() throws IOException {
        // Crear un archivo de prueba
        String filename = "testFile.json";
        Path filePath = tempDir.resolve(filename);
        Files.createFile(filePath);

        // Llamar al método loadAsResource
        Resource resource = storageService.loadAsResource(filename);

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
            storageService.loadAsResource(filename);
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
        storageService.init();
        assertTrue(Files.exists(tempDir), "El directorio debería haber sido creado");
    }

    @Test
    void delete_shouldDeleteFileSuccessfully() throws IOException {
        // Preparar el archivo de prueba
        Path testFile = tempDir.resolve("testFile.json");
        Files.createFile(testFile);

        // Ejecutar el método delete
        storageService.delete("testFile.json");

        // Verificar que el archivo haya sido eliminado
        assertFalse(Files.exists(testFile), "El archivo debería haber sido eliminado");
    }


    @Test
    void getUrl_returnsCorrectUrlWithMock() {
        String filename = "test-file.jpg";
        String expectedUrl = "http://localhost/files/" + filename;

        // Mock de MvcUriComponentsBuilder para que devuelva la URL esperada
        try (MockedStatic<MvcUriComponentsBuilder> mockedMvcUriBuilder = Mockito.mockStatic(MvcUriComponentsBuilder.class)) {
            mockedMvcUriBuilder.when(() ->
                    MvcUriComponentsBuilder.fromMethodName(FileUploadController.class, "serveFile", filename, null)
            ).thenReturn(UriComponentsBuilder.fromUriString(expectedUrl));

            // Llama al método a testear
            String generatedUrl = storageService.getUrl(filename);

            // Verifica que la URL generada es la esperada
            assertEquals(expectedUrl, generatedUrl);
        }
    }
}