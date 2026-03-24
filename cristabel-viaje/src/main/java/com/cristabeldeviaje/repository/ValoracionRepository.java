package com.cristabeldeviaje.repository;

import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.model.Usuario;
import com.cristabeldeviaje.model.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
    List<Valoracion> findByProducto(Producto producto);
    long countByProducto(Producto producto);
    boolean existsByProductoAndUsuario(Producto producto, Usuario usuario);
    Optional<Valoracion> findByProductoAndUsuario(Producto producto, Usuario usuario);
}