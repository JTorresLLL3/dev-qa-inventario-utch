package com.utch.devqa.service;

import com.utch.devqa.dto.ProductoRequest;
import com.utch.devqa.exception.DatoDuplicadoException;
import com.utch.devqa.exception.RecursoNoEncontradoException;
import com.utch.devqa.model.Producto;
import com.utch.devqa.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    private Producto productoBase;
    private ProductoRequest requestValido;

    @BeforeEach
    void configurarEscenario() {
        productoBase = new Producto();
        productoBase.setId("prod-01");
        productoBase.setSku("SKU-100");
        productoBase.setNombre("Monitor LED 24");
        productoBase.setDescripcion("Monitor Full HD IPS");
        productoBase.setPrecio(3200.0);
        productoBase.setStock(15);

        requestValido = new ProductoRequest(
                "SKU-100",
                "Monitor LED 24",
                "Monitor Full HD IPS",
                3200.0,
                15
        );
    }

    @Test
    @DisplayName("Debe retornar la lista completa de productos")
    void listarTodosExitoso() {
        when(repository.findAll()).thenReturn(List.of(productoBase));

        List<Producto> resultado = service.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("SKU-100", resultado.get(0).getSku());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Debe obtener un producto cuando el ID existe")
    void obtenerPorIdExitoso() {
        when(repository.findById("prod-01")).thenReturn(Optional.of(productoBase));

        Producto encontrado = service.obtenerPorId("prod-01");

        assertNotNull(encontrado);
        assertEquals("Monitor LED 24", encontrado.getNombre());
        assertEquals("Monitor Full HD IPS", encontrado.getDescripcion());
        assertEquals(3200.0, encontrado.getPrecio());
        assertEquals(15, encontrado.getStock());
    }

    @Test
    @DisplayName("Debe lanzar RecursoNoEncontradoException cuando el ID no existe")
    void obtenerPorIdNoEncontrado() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.obtenerPorId("inexistente"));
    }

    @Test
    @DisplayName("Debe crear un producto cuando el SKU no está duplicado")
    void crearProductoExitoso() {
        when(repository.existsBySku("SKU-100")).thenReturn(false);
        when(repository.save(any(Producto.class))).thenReturn(productoBase);

        Producto creado = service.crear(requestValido);

        assertNotNull(creado);
        assertEquals("prod-01", creado.getId());
        verify(repository).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe lanzar DatoDuplicadoException al crear con un SKU existente")
    void crearProductoConSkuDuplicado() {
        when(repository.existsBySku("SKU-100")).thenReturn(true);

        assertThrows(DatoDuplicadoException.class, () -> service.crear(requestValido));
        verify(repository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe actualizar un producto conservando su mismo SKU")
    void actualizarProductoMismoSkuExitoso() {
        ProductoRequest actualizacion = new ProductoRequest(
                "SKU-100",
                "Monitor Gamer 24",
                "144Hz IPS",
                3800.0,
                10
        );
        when(repository.findById("prod-01")).thenReturn(Optional.of(productoBase));
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto actualizado = service.actualizar("prod-01", actualizacion);

        assertEquals("Monitor Gamer 24", actualizado.getNombre());
        assertEquals(3800.0, actualizado.getPrecio());
        verify(repository).save(productoBase);
    }

    @Test
    @DisplayName("Debe actualizar un producto cuando cambia a un SKU nuevo disponible")
    void actualizarProductoNuevoSkuDisponible() {
        ProductoRequest actualizacion = new ProductoRequest(
                "SKU-200",
                "Monitor Gamer 27",
                "165Hz QHD",
                5200.0,
                8
        );
        when(repository.findById("prod-01")).thenReturn(Optional.of(productoBase));
        when(repository.existsBySku("SKU-200")).thenReturn(false);
        when(repository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto actualizado = service.actualizar("prod-01", actualizacion);

        assertEquals("SKU-200", actualizado.getSku());
        assertEquals(8, actualizado.getStock());
    }

    @Test
    @DisplayName("Debe lanzar DatoDuplicadoException al actualizar con un SKU que pertenece a otro producto")
    void actualizarProductoConSkuDuplicado() {
        ProductoRequest actualizacion = new ProductoRequest(
                "SKU-OCUPADO",
                "Monitor LED",
                "Full HD",
                3200.0,
                5
        );
        when(repository.findById("prod-01")).thenReturn(Optional.of(productoBase));
        when(repository.existsBySku("SKU-OCUPADO")).thenReturn(true);

        assertThrows(DatoDuplicadoException.class, () -> service.actualizar("prod-01", actualizacion));
        verify(repository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debe eliminar un producto existente")
    void eliminarProductoExitoso() {
        when(repository.findById("prod-01")).thenReturn(Optional.of(productoBase));

        service.eliminar("prod-01");

        verify(repository).delete(productoBase);
    }
}

