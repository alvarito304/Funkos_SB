package dev.alvaroherrero.funkosb.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.category.service.ICategoryService;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class CategoryRestControllerTest {
    private final String myEndpoint = "/categories";

    private final Category categoryTest = Category.builder()
            .id(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))
            .category(FunkoCategory.SERIE)
            .softDelete(false)
            .build();
    private final ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private ICategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<Category> jsonCategory;

    @Autowired
    public  CategoryRestControllerTest(ICategoryService categoryService) {
        this.categoryService = categoryService;
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllCategories() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(List.of(categoryTest));

        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        List<Category> res = mapper.readValue(response.getContentAsString(),
                mapper.getTypeFactory().constructCollectionType(List.class, Category.class));

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(categoryTest.getCategory(), res.get(0).getCategory())
        );

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    void getCategoryById() throws Exception {
        when(categoryService.getCategoryById(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(categoryTest);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        get(myEndpoint + "/{id}", UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        Category res = mapper.readValue(response.getContentAsString(), Category.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(categoryTest.getCategory(), res.getCategory())
        );
    }

    @Test
    void createCategory() throws Exception {
        when(categoryService.createCategory(categoryTest)).thenReturn(categoryTest);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(jsonCategory.write(categoryTest).getJson()))
                               .andReturn().getResponse();

        Category res = mapper.readValue(response.getContentAsString(), Category.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(categoryTest.getCategory(), res.getCategory())
        );
    }

    @Test
    void createCategoryInvalidParams() throws Exception {
        Category invalidCategory = Category.builder()
               .id(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0a"))
               .category(null)
               .softDelete(true)
               .build();

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        post(myEndpoint)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(jsonCategory.write(invalidCategory).getJson()))
                               .andReturn().getResponse();

        assertEquals(400, response.getStatus());
        assertTrue(response.getContentAsString().contains("Introduzca una categoria definida"));

        verify(categoryService, never()).createCategory(invalidCategory);
    }

    @Test
    void updateCategory() throws Exception {
        when(categoryService.updateCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"), categoryTest)).thenReturn(categoryTest);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        put(myEndpoint + "/{id}", UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(jsonCategory.write(categoryTest).getJson()))
                               .andReturn().getResponse();

        Category res = mapper.readValue(response.getContentAsString(), Category.class);

        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(categoryTest.getCategory(), res.getCategory())
        );
        verify(categoryService, times(1)).updateCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"), categoryTest);
    }

    @Test
    void deleteCategory() throws Exception {
        when(categoryService.softDeleteCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))).thenReturn(categoryTest);

        // Consulto el endpoint
        MockHttpServletResponse response = mockMvc.perform(
                        delete(myEndpoint + "/{id}", UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"))
                               .contentType(MediaType.APPLICATION_JSON))
                               .andReturn().getResponse();

        assertEquals(204, response.getStatus());
        verify(categoryService, times(1)).softDeleteCategory(UUID.fromString("1aa1b369-beed-4eef-b63c-9f6330c71b0f"));
    }
}