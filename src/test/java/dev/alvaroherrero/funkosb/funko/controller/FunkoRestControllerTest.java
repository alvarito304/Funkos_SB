package dev.alvaroherrero.funkosb.funko.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.funko.dto.FunkoDTO;
import dev.alvaroherrero.funkosb.funko.mapper.FunkoMapper;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.funko.service.IFunkoService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class FunkoRestControllerTest {
    private  final String myEndpoint = "/funkos";
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

    @Autowired
    private JacksonTester<Funko> jsonFunko;
    @Autowired
    private JacksonTester<FunkoDTO> jsonFunkoDto;

    @Autowired
    MockMvc mockMvc; // Cliente MVC
    @MockBean
    private IFunkoService funkoService;
    @Autowired
    private FunkoMapper funkoMapper;

    @Autowired
    public FunkoRestControllerTest(IFunkoService funkoService) {
        this.funkoService = funkoService;
        mapper.registerModule(new JavaTimeModule());
    }


//    @Test
//    void getAllFunkos() throws Exception {
//        when(funkoService.getFunkos()).thenReturn(List.of(funkoTest));
//
//        // Consulto el endpoint
//        MockHttpServletResponse response = mockMvc.perform(
//                        get(myEndpoint)
//                                .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        List<FunkoDTO> res = mapper.readValue(response.getContentAsString(),
//                mapper.getTypeFactory().constructCollectionType(List.class, FunkoDTO.class));
//
//        assertAll(
//                () -> assertEquals(200, response.getStatus()),
//                () -> assertEquals(1, res.size()),
//                () -> assertEquals(funkoTest.getName(), res.get(0).getName()),
//                () -> assertEquals(funkoTest.getPrice(), res.get(0).getPrice()),
//                () -> assertEquals(funkoTest.getCategory(), res.get(0).getCategory())
//        );
//
//        verify(funkoService, times(1)).getFunkos();
//    }

    @Test
    void getFunkosByName() throws Exception {
        when(funkoService.getFunkosByName(funkoTest.getName())).thenReturn(List.of(funkoTest));

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/name/" + funkoTest.getName())
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<FunkoDTO> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, FunkoDTO.class));

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.size()),
                () -> assertEquals(funkoTest.getName(), res.get(0).getName()),
                () -> assertEquals(funkoTest.getPrice(), res.get(0).getPrice()),
                () -> assertEquals(funkoTest.getCategory(), res.get(0).getCategory())
        );

        verify(funkoService, times(1)).getFunkosByName(funkoTest.getName());
    }

    @Test
    void getFunkoById() throws Exception {
        when(funkoService.getFunkoById(0L)).thenReturn(funkoTest);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/{id}", 0L)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        FunkoDTO res = mapper.readValue(response.getContentAsString(), FunkoDTO.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funkoTest.getName(), res.getName()),
                () -> assertEquals(funkoTest.getPrice(), res.getPrice()),
                () -> assertEquals(funkoTest.getCategory(), res.getCategory())
        );

        verify(funkoService, times(1)).getFunkoById(0L);
    }

    @Test
    void createFunko() throws Exception {
        when(funkoService.createFunko(any())).thenReturn(funkoTest);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunko.write(funkoTest).getJson()))
                .andReturn().getResponse();

        FunkoDTO res = mapper.readValue(response.getContentAsString(), FunkoDTO.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funkoTest.getName(), res.getName()),
                () -> assertEquals(funkoTest.getPrice(), res.getPrice()),
                () -> assertEquals(funkoTest.getCategory(), res.getCategory())
        );

        verify(funkoService, times(1)).createFunko(any());
    }

    @Test
    void createFunkoInvalid() throws Exception {
        funkoTest.setName(null);
        funkoTest.setPrice(-1.0f);
        funkoTest.setCategory(null);
        when(funkoService.createFunko(funkoTest)).thenReturn(funkoTest);

        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunko.write(funkoTest).getJson()))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();


        assertAll(
                () -> assertTrue(response.getContentAsString().contains("El nombre no puede estar vacio")),
                () -> assertTrue(response.getContentAsString().contains("El precio debe ser un mayor que 0"))
        );
        verify(funkoService, never()).createFunko(any());

    }

    @Test
    void updateFunko() throws Exception {
        when(funkoService.updateFunko(any(), any())).thenReturn(funkoTest);

        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", 0L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonFunko.write(funkoTest).getJson()))
                .andReturn().getResponse();

        FunkoDTO res = mapper.readValue(response.getContentAsString(), FunkoDTO.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(funkoTest.getName(), res.getName()),
                () -> assertEquals(funkoTest.getPrice(), res.getPrice()),
                () -> assertEquals(funkoTest.getCategory(), res.getCategory())
        );

        verify(funkoService, times(1)).updateFunko(any(), any());
    }

    @Test
    void deleteFunko() throws Exception {
        when(funkoService.deleteFunko(any())).thenReturn(funkoTest);

        mockMvc.perform(delete(myEndpoint + "/{id}", 0L))
                .andExpect(status().isOk());

        verify(funkoService, times(1)).deleteFunko(any());
    }

    @Test
    void nuevoProducto() throws Exception {
        when(funkoService.updateImage(any(), any())).thenReturn(funkoTest);

        mockMvc.perform(multipart(myEndpoint + "/imagen/{id}", funkoTest.getId())  // Usar el id en la URL
                        .file(new MockMultipartFile("file", "image.jpg", "image/jpeg", "image".getBytes()))  // Enviar el archivo correctamente
                        .with(request -> {
                            request.setMethod("PATCH");  // Especificamos el método PATCH
                            return request;
                        }))
                .andExpect(status().isOk());

        verify(funkoService, times(1)).updateImage(any(), any());
    }

}