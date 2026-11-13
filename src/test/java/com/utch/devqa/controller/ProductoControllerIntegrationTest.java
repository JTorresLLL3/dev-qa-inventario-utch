package com.utch.devqa.controller;

import com.utch.devqa.dto.ProductoRequest;
import com.utch.devqa.exception.DatoDuplicadoException;
import com.utch.devqa.exception.RecursoNoEncontradoException;
import com.utch.devqa.model.Producto;
import com.utch.devqa.service.ProductoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    @Test
    @DisplayName("GET /api/productos - Debe responder 200 con lista JSON")
    void consultarCatalogoDebeResponder200() throws Exception {
        Producto muestra = new Producto("p1", "SKU-01", "Teclado Mecánico", "Switch Red", 1200.0, 20);
        when(productoService.listarTodos()).thenReturn(List.of(muestra));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("p1"))
                .andExpect(jsonPath("$[0].sku").value("SKU-01"))
                .andExpect(jsonPath("$[0].nombre").value("Teclado Mecánico"));
    }

    @Test
    @DisplayName("GET /api/productos/{id} - Debe responder 200 cuando existe")
    void buscarProductoPorIdDebeResponder200() throws Exception {
        Producto muestra = new Producto("p1", "SKU-01", "Teclado Mecánico", "Switch Red", 1200.0, 20);
        when(productoService.obtenerPorId("p1")).thenReturn(muestra);

        mockMvc.perform(get("/api/productos/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-01"));
    }

    @Test
    @DisplayName("GET /api/productos/{id} - Debe responder 404 cuando no existe")
    void buscarProductoPorIdInexistenteDebeResponder404() throws Exception {
        when(productoService.obtenerPorId("no-existe"))
                .thenThrow(new RecursoNoEncontradoException("Producto no encontrado con ID: no-existe"));

        mockMvc.perform(get("/api/productos/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigoEstado").value(404))
                .andExpect(jsonPath("$.tipoError").value("Recurso no encontrado"));
    }

    @Test
    @DisplayName("POST /api/productos - Debe responder 201 al registrar un producto válido")
    void registrarProductoValidoDebeResponder201() throws Exception {
        Producto creado = new Producto("p2", "SKU-02", "Mouse Inalámbrico", "Sensor óptico", 650.0, 30);
        when(productoService.crear(any(ProductoRequest.class))).thenReturn(creado);

        String jsonPayload = """
                {
                  "sku": "SKU-02",
                  "nombre": "Mouse Inalámbrico",
                  "descripcion": "Sensor óptico",
                  "precio": 650.0,
                  "stock": 30
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("p2"))
                .andExpect(jsonPath("$.sku").value("SKU-02"));
    }

    @Test
    @DisplayName("POST /api/productos - Debe responder 400 cuando fallan las validaciones")
    void registrarProductoInvalidoDebeResponder400() throws Exception {
        String jsonInvalido = """
                {
                  "sku": "",
                  "nombre": "",
                  "descripcion": "Descripción válida",
                  "precio": -10.0,
                  "stock": -5
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigoEstado").value(400))
                .andExpect(jsonPath("$.camposInvalidos.sku").exists())
                .andExpect(jsonPath("$.camposInvalidos.precio").exists());
    }

    @Test
    @DisplayName("POST /api/productos - Debe responder 409 cuando el SKU ya existe")
    void registrarProductoDuplicadoDebeResponder409() throws Exception {
        when(productoService.crear(any(ProductoRequest.class)))
                .thenThrow(new DatoDuplicadoException("Ya existe un producto con el SKU: SKU-02"));

        String jsonPayload = """
                {
                  "sku": "SKU-02",
                  "nombre": "Mouse Inalámbrico",
                  "descripcion": "Sensor óptico",
                  "precio": 650.0,
                  "stock": 30
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigoEstado").value(409))
                .andExpect(jsonPath("$.tipoError").value("Conflicto de datos duplicados"));
    }

    @Test
    @DisplayName("PUT /api/productos/{id} - Debe responder 200 al actualizar")
    void modificarProductoDebeResponder200() throws Exception {
        Producto actualizado = new Producto("p1", "SKU-01", "Teclado Pro", "RGB", 1500.0, 12);
        when(productoService.actualizar(eq("p1"), any(ProductoRequest.class))).thenReturn(actualizado);

        String jsonPayload = """
                {
                  "sku": "SKU-01",
                  "nombre": "Teclado Pro",
                  "descripcion": "RGB",
                  "precio": 1500.0,
                  "stock": 12
                }
                """;

        mockMvc.perform(put("/api/productos/p1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Teclado Pro"))
                .andExpect(jsonPath("$.precio").value(1500.0));
    }

    @Test
    @DisplayName("DELETE /api/productos/{id} - Debe responder 204 al eliminar")
    void borrarProductoDebeResponder204() throws Exception {
        doNothing().when(productoService).eliminar("p1");

        mockMvc.perform(delete("/api/productos/p1"))
                .andExpect(status().isNoContent());
    }
}

