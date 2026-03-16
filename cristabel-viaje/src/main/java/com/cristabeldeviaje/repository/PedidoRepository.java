package com.cristabeldeviaje.repository;

import com.cristabeldeviaje.model.Pedido;
import com.cristabeldeviaje.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Para buscar todos los pedidos de un usuario específico (mis_pedidos.php)
    List<Pedido> findByUsuario(Usuario usuario);
}