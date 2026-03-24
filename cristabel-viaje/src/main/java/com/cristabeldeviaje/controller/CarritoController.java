package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.repository.ProductoRepository;
import com.cristabeldeviaje.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public String verCarrito(Model model, HttpSession session) {
        String rol = (String) session.getAttribute("usuario_rol");

        if ("admin".equalsIgnoreCase(rol)) {
            return "redirect:/?adminNoCompra";
        }

        model.addAttribute("items", carritoService.getItems());
        model.addAttribute("total", carritoService.getTotal());
        return "carrito";
    }

    @PostMapping("/add")
    public String agregarAlCarrito(@RequestParam Long id,
                                   @RequestParam(required = false) String talla,
                                   @RequestParam(required = false) String color,
                                   @RequestParam(defaultValue = "1") Integer cantidad,
                                   HttpSession session) {

        String rol = (String) session.getAttribute("usuario_rol");

        if ("admin".equalsIgnoreCase(rol)) {
            return "redirect:/producto/" + id + "?adminNoCompra";
        }

        Producto producto = productoRepository.findById(id).orElse(null);

        if (producto != null) {
            boolean tieneTallas = producto.getTallas() != null && !producto.getTallas().isBlank();
            boolean tieneColores = producto.getColores() != null && !producto.getColores().isBlank();

            if ((tieneTallas && (talla == null || talla.isBlank())) ||
                    (tieneColores && (color == null || color.isBlank()))) {
                return "redirect:/producto/" + id + "?errorOpciones";
            }

            carritoService.agregarProducto(producto, talla, color, cantidad);
        }

        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam String clave,
                                     @RequestParam Integer cantidad,
                                     HttpSession session) {
        String rol = (String) session.getAttribute("usuario_rol");

        if ("admin".equalsIgnoreCase(rol)) {
            return "redirect:/?adminNoCompra";
        }

        carritoService.actualizarCantidad(clave, cantidad);
        return "redirect:/carrito";
    }

    @GetMapping("/eliminar")
    public String eliminarProducto(@RequestParam String clave,
                                   HttpSession session) {
        String rol = (String) session.getAttribute("usuario_rol");

        if ("admin".equalsIgnoreCase(rol)) {
            return "redirect:/?adminNoCompra";
        }

        carritoService.eliminarProducto(clave);
        return "redirect:/carrito";
    }
}