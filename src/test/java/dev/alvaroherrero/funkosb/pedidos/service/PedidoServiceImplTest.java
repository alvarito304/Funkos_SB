package dev.alvaroherrero.funkosb.pedidos.service;

import dev.alvaroherrero.funkosb.category.model.Category;
import dev.alvaroherrero.funkosb.funko.model.Funko;
import dev.alvaroherrero.funkosb.funko.service.IFunkoService;
import dev.alvaroherrero.funkosb.global.types.funkocategory.FunkoCategory;
import dev.alvaroherrero.funkosb.pedidos.exceptions.PedidoNoItems;
import dev.alvaroherrero.funkosb.pedidos.exceptions.ProductoBadPrice;
import dev.alvaroherrero.funkosb.pedidos.exceptions.ProductoNotStock;
import dev.alvaroherrero.funkosb.pedidos.models.Cliente;
import dev.alvaroherrero.funkosb.pedidos.models.Direccion;
import dev.alvaroherrero.funkosb.pedidos.models.LineaPedido;
import dev.alvaroherrero.funkosb.pedidos.models.Pedido;
import dev.alvaroherrero.funkosb.pedidos.repository.IPedidosRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PedidosServiceTest {

    @MockBean
    private IFunkoService funkoService; // Simula el bean.

    @MockBean
    private IPedidosRepository pedidosRepository;

    @Autowired
    private IPedidoService pedidosService;

    private Pedido pedido1;
    private Pedido pedido2;

    private Funko funkoTest;
    private Category categoryTest;

    @BeforeEach
    void setUp() {


        categoryTest = new Category();
        categoryTest.setCategory(FunkoCategory.SERIE);

        funkoTest = new Funko();
        funkoTest.setId(2L);
        funkoTest.setName("Test Funko");
        funkoTest.setPrice(17.45F);
        funkoTest.setCategory(categoryTest);
        // Configurar datos de prueba
        Direccion direccion = Direccion.builder()
                .calle("calle")
                .numero("numero")
                .ciudad("ciudad")
                .provincia("provincia")
                .pais("pais")
                .codigoPostal("28054")
                .build();

        // Construir Cliente
        Cliente cliente = Cliente.builder()
                .nombreCompleto("prueba1")
                .email("alvaro@gmail.com")
                .telefono("999999999")
                .direccion(direccion)
                .build();

        // Construir LineaPedido
        LineaPedido lineaPedido = LineaPedido.builder()
                .idProducto(2L)
                .precioProducto(17.45)
                .build();

        // Construir Pedido
         pedido1 = Pedido.builder()
                .id(new ObjectId("673904d895cf041dff57cf29"))
                .idUsuario(2L)
                .cliente(cliente)
                .lineasPedido(Arrays.asList(lineaPedido))
                .build();

         pedido2 = Pedido.builder()
                .id(new ObjectId("64b2899f8fc4ec047563e9a2"))
                .idUsuario(2L)
                .build();
    }

    @Test
    @DisplayName("Debe retornar una página de pedidos")
    void testFindAll() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2); // Página 0, tamaño 2
        Page<Pedido> mockPage = new PageImpl<>(Arrays.asList(pedido1, pedido2));
        when(pedidosRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        // Act
        Page<Pedido> result = pedidosService.findAll(pageable);

        // Assert
        assertAll(
                () -> assertEquals(2, result.getTotalElements())
        );
    }

    @Test
    @DisplayName("Debe retornar un pedido por id")
    void testFindById() {
        // Arrange
        when(pedidosRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(pedido1));

        // Act
        Pedido result = pedidosService.findById(new ObjectId("673904d895cf041dff57cf29"));

        // Assert
        assertAll(
                () -> assertEquals(new ObjectId("673904d895cf041dff57cf29"), result.getId())
        );
    }


    @Test
    @DisplayName("Debe retornar una página de pedidos para un usuario")
    void testFindByIdUsuario() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2); // Página 0, tamaño 2
        Page<Pedido> mockPage = new PageImpl<>(Arrays.asList(pedido1));
        when(pedidosRepository.findByIdUsuario(anyLong(), any(Pageable.class))).thenReturn(mockPage);

        // Act
        Page<Pedido> result = pedidosService.findByIdUsuario(2L, pageable);

        // Assert
        assertAll(
                () -> assertEquals(1, result.getTotalElements())
        );
    }

    @Test
    @DisplayName("Debe guardar un nuevo pedido")
    void testSave() {
        // Arrange
        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedido1);

        // Mockea el comportamiento de funkoService para devolver un Funko válido
        when(funkoService.getFunkoById(anyLong())).thenReturn(funkoTest);  // Devuelve un funko con stock

        // Act
        Pedido result = pedidosService.save(pedido1);

        // Assert
        assertAll(
                () -> assertEquals(new ObjectId("673904d895cf041dff57cf29"), result.getId())
        );
    }

    @Test
    void returnStockPedido_LineasPedidoIsEmpty() {

        // Arrange
        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedido2);

        // Mockea el comportamiento de funkoService para devolver un Funko válido
        when(funkoService.getFunkoById(anyLong())).thenReturn(funkoTest);  // Devuelve un funko con stock

        // Assert
        assertThrows(PedidoNoItems.class, () -> pedidosService.save(pedido2));
        verify(pedidosRepository, times(0)).save(any(Pedido.class));
    }

    @Test
    void checkPedido_no_hay_stock() {
        funkoTest.setStock(0);
        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedido1);
        when(funkoService.getFunkoById(anyLong())).thenReturn(funkoTest);  // Devuelve un funko con stock

        // Mockea el comportamiento de funkoService para devolver un Funko válido
        when(funkoService.getFunkoById(anyLong())).thenReturn(funkoTest);  // Devuelve un funko con stock

        // Assert
        assertThrows(ProductoNotStock.class, () -> pedidosService.save(pedido1));
        verify(pedidosRepository, times(0)).save(any(Pedido.class));
    }

    @Test
    void checkPedido_BadPrice() {
        funkoTest.setPrice(10000.00F);
        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedido1);
        when(funkoService.getFunkoById(anyLong())).thenReturn(funkoTest);  // Devuelve un funko con stock

        // Mockea el comportamiento de funkoService para devolver un Funko válido
        when(funkoService.getFunkoById(anyLong())).thenReturn(funkoTest);  // Devuelve un funko con stock

        // Assert
        assertThrows(ProductoBadPrice.class, () -> pedidosService.save(pedido1));
        verify(pedidosRepository, times(0)).save(any(Pedido.class));
    }

    @Test
    void delete() {
        when(pedidosRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(pedido1));
        doNothing().when(pedidosRepository).delete(any(Pedido.class));  // Mockea delete con Pedido
        when(funkoService.getFunkoById(anyLong())).thenReturn(funkoTest);  // Devuelve un funko con stock

        pedidosService.delete(pedido1.getId());

        verify(pedidosRepository, times(1)).delete(any(Pedido.class));  // Verifica que delete fue invocado
    }


    @Test
    void update() {
        // Establece otros atributos de pedidoActual según sea necesario
        Pedido pedidoActualizado = pedido1;
        pedidoActualizado.setIdUsuario(4L);

        // Mock del repositorio y otros servicios
        when(pedidosRepository.findById(pedido1.getId())).thenReturn(Optional.of(pedido1));  // Cuando se busca el pedido
        when(pedidosRepository.save(any(Pedido.class))).thenReturn(pedidoActualizado);  // Cuando se guarda el pedido actualizado
        when(funkoService.getFunkoById(anyLong())).thenReturn(funkoTest);  // Devuelve un funko con stock


        // Llamada al método de servicio
        Pedido resultado = pedidosService.update(pedido1.getId(), pedidoActualizado);

        // Verificar interacciones
        verify(pedidosRepository, times(1)).findById(pedido1.getId());  // Verifica que findById fue llamado una vez
        verify(pedidosRepository, times(1)).save(pedidoActualizado);  // Verifica que save fue llamado una vez

        // Verificar que el resultado es el pedido actualizado
        assertEquals(pedidoActualizado, resultado);
    }

}