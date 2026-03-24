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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/")
    public String listarProductos(Model model) {
        List<Producto> productos = productoRepository.findAll();

        Map<Long, Double> medias = new HashMap<>();

        for (Producto p : productos) {
            List<Valoracion> valoraciones = valoracionRepository.findByProducto(p);

            double media = 0;
            if (!valoraciones.isEmpty()) {
                media = valoraciones.stream()
                        .mapToInt(Valoracion::getPuntuacion)
                        .average()
                        .orElse(0);
            }

            medias.put(p.getId(), media);
        }

        model.addAttribute("productos", productos);
        model.addAttribute("medias", medias);

        return "index";
    }

    @GetMapping("/producto/{id}")
    public String verDetalle(@PathVariable Long id, Model model, HttpSession session) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/";
        }

        List<Valoracion> valoraciones = valoracionRepository.findByProducto(producto);
        long totalValoraciones = valoracionRepository.countByProducto(producto);

        double media = 0;
        if (!valoraciones.isEmpty()) {
            media = valoraciones.stream()
                    .mapToInt(Valoracion::getPuntuacion)
                    .average()
                    .orElse(0);
        }

        boolean puedeValorar = false;
        boolean yaValoro = false;
        Valoracion miValoracion = null;

        String email = (String) session.getAttribute("usuario_email");
        if (email != null) {
            Usuario usuario = usuarioRepository.findByEmail(email);
            if (usuario != null) {
                miValoracion = valoracionRepository.findByProductoAndUsuario(producto, usuario).orElse(null);
                yaValoro = miValoracion != null;
                puedeValorar = pedidoRepository.existsPedidoEntregadoConProducto(usuario, id) && !yaValoro;
            }
        }

        // 🔥 PRODUCTOS RELACIONADOS
        List<Producto> relacionados = productoRepository
                .findTop4ByCategoriaAndIdNot(producto.getCategoria(), producto.getId());

        model.addAttribute("producto", producto);
        model.addAttribute("valoraciones", valoraciones);
        model.addAttribute("mediaValoracion", media);
        model.addAttribute("totalValoraciones", totalValoraciones);
        model.addAttribute("puedeValorar", puedeValorar);
        model.addAttribute("yaValoro", yaValoro);
        model.addAttribute("miValoracion", miValoracion);
        model.addAttribute("relacionados", relacionados);

        return "detalle";
    }
}