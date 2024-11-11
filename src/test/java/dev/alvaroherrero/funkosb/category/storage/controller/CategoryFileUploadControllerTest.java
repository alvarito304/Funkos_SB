package dev.alvaroherrero.funkosb.category.storage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.alvaroherrero.funkosb.category.dto.CategoryDTO;
import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.category.storage.service.ICategoryStorageService;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.funko.storage.service.IStorageService;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.Resource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class CategoryFileUploadControllerTest {

    private final String myEndpoint = "/category/files";

    private final Category categoryTest = Category.builder()
            .id(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))
            .category(FunkoCategory.SERIE)
            .softDelete(false)
            .build();
    private final CategoryDTO categoryDtoTest = new CategoryDTO(FunkoCategory.SERIE);

    private  final Funko funkoTest = Funko.builder()
            .id(1L)
            .category(categoryTest)
            .price(10.0f)
            .name("Test Funko")
            .image("image")
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private ICategoryStorageService storageService;

    @Autowired
    MockMvc mockMvc;

//    @Autowired
//    private JacksonTester<Category> jsonFunko;

    @Autowired
    public CategoryFileUploadControllerTest(ICategoryStorageService storageService) {
        this.storageService = storageService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void uploadFile() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", // Nombre del parámetro del archivo
                "test.json",
                MediaType.APPLICATION_JSON_VALUE,
                "Contenido del archivo".getBytes()
        );

        when(storageService.readJson(any(MultipartFile.class))).thenReturn(List.of(categoryDtoTest));

        // Cambia la llamada a mockMvc para usar multipart()
        MockHttpServletResponse response = mockMvc.perform(
                multipart(myEndpoint)
                        .file(mockFile)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andReturn().getResponse();

        assertEquals(200, response.getStatus());
        verify(storageService, times(1)).readJson(any());
    }

    @Test
    void uploadFile_BadRequest() throws Exception {
        // Cambia la llamada a mockMvc para usar multipart()
        MockHttpServletResponse response = mockMvc.perform(
                multipart(myEndpoint)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
        ).andReturn().getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    void serveDefaultFile() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.json", MediaType.APPLICATION_JSON_VALUE, "Contenido del archivo".getBytes()
        );
        String defaultFilename = "default.json";
        when(storageService.loadAsResource(defaultFilename)).thenReturn(mockFile.getResource());
        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint )
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                )
                .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        assertTrue(response.getHeader("Content-Disposition").contains("categories.json"));
        verify(storageService, times(1)).loadAsResource(any());
    }

}