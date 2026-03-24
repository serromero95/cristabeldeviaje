package com.cristabeldeviaje.repository;

import com.cristabeldeviaje.model.Pedido;
import com.cristabeldeviaje.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuario(Usuario usuario);

    List<Pedido> findByUsuarioOrderByFechaDesc(Usuario usuario);

    List<Pedido> findAllByOrderByFechaDesc();

    @Query("""
        SELECT COUNT(p) > 0
        FROM Pedido p
        JOIN p.detalles d
        WHERE p.usuario = :usuario
          AND p.estado = 'entregado'
          AND d.producto.id = :productoId
    """)
    boolean existsPedidoEntregadoConProducto(@Param("usuario") Usuario usuario,
                                             @Param("productoId") Long productoId);
}