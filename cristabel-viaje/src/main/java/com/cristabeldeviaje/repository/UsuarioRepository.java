package com.cristabeldeviaje.repository;

import com.cristabeldeviaje.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Esto nos permite buscar usuarios por email
    Usuario findByEmail(String email);
}