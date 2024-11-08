package dev.alvaroherrero.funkosb.funko.storage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.alvaroherrero.funkosb.category.model.Category;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class FileUploadControllerTest {
    private final String myEndpoint = "/funkos/files";

    private final Category categoryTest = Category.builder()
            .id(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))
            .category(FunkoCategory.SERIE)
            .softDelete(false)
            .build();

    private  final Funko funkoTest = Funko.builder()
            .id(1L)
            .category(categoryTest)
            .price(10.0f)
            .name("Test Funko")
            .image("image")
            .build();
    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private IStorageService storageService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private JacksonTester<Funko> jsonFunko;

    @Autowired
    public FileUploadControllerTest(IStorageService storageService) {
        this.storageService = storageService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void serveFile() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "test.jpeg", MediaType.IMAGE_JPEG_VALUE, "Contenido del archivo".getBytes()
        );
        when(storageService.loadAsResource(any())).thenReturn(mockFile.getResource());
        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                get(myEndpoint + "/{filename}", "test.jpeg")
                       .contentType(MediaType.APPLICATION_JSON)
                       .accept(MediaType.IMAGE_JPEG)
                )
               .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        verify(storageService, times(1)).loadAsResource(any());
    }
}