package com.utch.devqa.service;

import com.utch.devqa.dto.ProductoRequest;
import com.utch.devqa.exception.DatoDuplicadoException;
import com.utch.devqa.exception.RecursoNoEncontradoException;
import com.utch.devqa.model.Producto;
import com.utch.devqa.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listarTodos() {
        return repository.findAll();
    }

    public Producto obtenerPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con ID: " + id));
    }

    public Producto crear(ProductoRequest request) {
        if (repository.existsBySku(request.sku())) {
            throw new DatoDuplicadoException("Ya existe un producto con el SKU: " + request.sku());
        }

        Producto producto = new Producto(
                null,
                request.sku(),
                request.nombre(),
                request.descripcion(),
                request.precio(),
                request.stock()
        );
        return repository.save(producto);
    }

    public Producto actualizar(String id, ProductoRequest request) {
        Producto existente = obtenerPorId(id);

        if (!existente.getSku().equals(request.sku()) && repository.existsBySku(request.sku())) {
            throw new DatoDuplicadoException("Ya existe un producto con el SKU: " + request.sku());
        }

        existente.setSku(request.sku());
        existente.setNombre(request.nombre());
        existente.setDescripcion(request.descripcion());
        existente.setPrecio(request.precio());
        existente.setStock(request.stock());

        return repository.save(existente);
    }

    public void eliminar(String id) {
        Producto existente = obtenerPorId(id);
        repository.delete(existente);
    }
}

