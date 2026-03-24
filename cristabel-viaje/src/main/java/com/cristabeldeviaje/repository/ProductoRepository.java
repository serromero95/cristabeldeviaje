package com.cristabeldeviaje.repository;

import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    long countByCategoria(Categoria categoria);

    List<Producto> findTop4ByCategoriaAndIdNot(Categoria categoria, Long id);
}