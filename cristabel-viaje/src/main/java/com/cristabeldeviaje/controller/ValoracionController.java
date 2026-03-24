package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.model.Usuario;
import com.cristabeldeviaje.model.Valoracion;
import com.cristabeldeviaje.repository.PedidoRepository;
import com.cristabeldeviaje.repository.ProductoRepository;
import com.cristabeldeviaje.repository.UsuarioRepository;
import com.cristabeldeviaje.repository.ValoracionRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class ValoracionController {

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @PostMapping("/producto/{id}/valorar")
    public String guardarValoracion(@PathVariable Long id,
                                    @RequestParam Integer puntuacion,
                                    @RequestParam String comentario,
                                    HttpSession session) {

        String email = (String) session.getAttribute("usuario_email");

        if (email == null) {
            return "redirect:/login";
        }

        Producto producto = productoRepository.findById(id).orElse(null);
        Usuario usuario = usuarioRepository.findByEmail(email);

        if (producto == null || usuario == null) {
            return "redirect:/";
        }

        if (!pedidoRepository.existsPedidoEntregadoConProducto(usuario, id)) {
            return "redirect:/producto/" + id + "?valoracionNoPermitida";
        }

        if (valoracionRepository.existsByProductoAndUsuario(producto, usuario)) {
            return "redirect:/producto/" + id + "?valoracionDuplicada";
        }

        Valoracion valoracion = new Valoracion();
        valoracion.setProducto(producto);
        valoracion.setUsuario(usuario);
        valoracion.setPuntuacion(puntuacion);
        valoracion.setComentario(comentario);
        valoracion.setFecha(LocalDateTime.now());

        valoracionRepository.save(valoracion);

        return "redirect:/producto/" + id + "?valoracionOk";
    }

    @PostMapping("/producto/{productoId}/valoracion/{valoracionId}/editar")
    public String editarValoracion(@PathVariable Long productoId,
                                   @PathVariable Long valoracionId,
                                   @RequestParam Integer puntuacion,
                                   @RequestParam String comentario,
                                   HttpSession session) {

        String email = (String) session.getAttribute("usuario_email");
        if (email == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findByEmail(email);
        Valoracion valoracion = valoracionRepository.findById(valoracionId).orElse(null);

        if (usuario == null || valoracion == null) {
            return "redirect:/producto/" + productoId;
        }

        if (!valoracion.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/producto/" + productoId;
        }

        valoracion.setPuntuacion(puntuacion);
        valoracion.setComentario(comentario);
        valoracion.setFecha(LocalDateTime.now());

        valoracionRepository.save(valoracion);

        return "redirect:/producto/" + productoId + "?valoracionEditada";
    }

    @PostMapping("/producto/{productoId}/valoracion/{valoracionId}/eliminar")
    public String eliminarValoracion(@PathVariable Long productoId,
                                     @PathVariable Long valoracionId,
                                     HttpSession session) {

        String email = (String) session.getAttribute("usuario_email");
        if (email == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findByEmail(email);
        Valoracion valoracion = valoracionRepository.findById(valoracionId).orElse(null);

        if (usuario == null || valoracion == null) {
            return "redirect:/producto/" + productoId;
        }

        if (!valoracion.getUsuario().getId().equals(usuario.getId())) {
            return "redirect:/producto/" + productoId;
        }

        valoracionRepository.delete(valoracion);

        return "redirect:/producto/" + productoId + "?valoracionEliminada";
    }
}