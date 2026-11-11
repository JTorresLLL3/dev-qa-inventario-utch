package com.utch.devqa.repository;

import com.utch.devqa.model.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductoRepository extends MongoRepository<Producto, String> {

    boolean existsBySku(String sku);
}
