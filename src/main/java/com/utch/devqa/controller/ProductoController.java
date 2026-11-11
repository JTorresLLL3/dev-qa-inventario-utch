package com.utch.devqa.controller;

import com.utch.devqa.dto.ProductoRequest;
import com.utch.devqa.model.Producto;
import com.utch.devqa.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@Tag(name = "Inventario UTCH", description = "Operaciones CRUD de productos para práctica HU-01")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @Operation(summary = "Consultar catálogo completo de productos")
    public ResponseEntity<List<Producto>> consultarCatalogo() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar un producto específico por su ID")
    public ResponseEntity<Producto> buscarProductoPorId(@PathVariable String id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo producto en el inventario")
    public ResponseEntity<Producto> registrarProducto(@Valid @RequestBody ProductoRequest payload) {
        Producto creado = productoService.crear(payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar los datos de un producto existente")
    public ResponseEntity<Producto> modificarProducto(
            @PathVariable String id,
            @Valid @RequestBody ProductoRequest payload) {
        return ResponseEntity.ok(productoService.actualizar(id, payload));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Dar de baja un producto por su ID")
    public ResponseEntity<Void> borrarProducto(@PathVariable String id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

