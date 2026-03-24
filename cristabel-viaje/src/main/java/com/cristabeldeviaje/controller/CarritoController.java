package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.CarritoItem;
import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.repository.ProductoRepository;
import com.cristabeldeviaje.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

        List<CarritoItem> items = new ArrayList<>(carritoService.getItems());
        double total = carritoService.getTotal();

        double descuento = 0.0;

        boolean tienePackPrincipiantes = contieneProductos(items, 14L, 15L, 10L);
        boolean tienePackCristabel = contieneProductos(items, 12L, 7L, 13L);

        if (tienePackPrincipiantes) {
            descuento += calcularDescuentoPack(items, 14L, 15L, 10L, 0.10);
        }

        if (tienePackCristabel) {
            descuento += calcularDescuentoPack(items, 12L, 7L, 13L, 0.15);
        }

        double totalFinal = total - descuento;

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("descuento", descuento);
        model.addAttribute("totalFinal", totalFinal);
        model.addAttribute("packPrincipiantesAplicado", tienePackPrincipiantes);
        model.addAttribute("packCristabelAplicado", tienePackCristabel);

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

    @PostMapping("/add-pack")
    public String agregarPack(@RequestParam String pack,
                              HttpSession session) {

        String rol = (String) session.getAttribute("usuario_rol");

        if ("admin".equalsIgnoreCase(rol)) {
            return "redirect:/?adminNoCompra";
        }

        List<Long> ids;

        if ("principiantes".equalsIgnoreCase(pack)) {
            ids = List.of(14L, 15L, 10L);
        } else if ("cristabel".equalsIgnoreCase(pack)) {
            ids = List.of(12L, 7L, 13L);
        } else {
            return "redirect:/packs";
        }

        List<Producto> productos = productoRepository.findAllById(ids);

        for (Producto producto : productos) {
            carritoService.agregarProducto(producto, null, null, 1);
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

    private boolean contieneProductos(List<CarritoItem> items, Long... ids) {
        for (Long id : ids) {
            boolean encontrado = items.stream()
                    .anyMatch(item -> item.getProducto().getId().equals(id));

            if (!encontrado) {
                return false;
            }
        }
        return true;
    }

    private double calcularDescuentoPack(List<CarritoItem> items, Long id1, Long id2, Long id3, double porcentaje) {
        double totalPack = items.stream()
                .filter(item -> item.getProducto().getId().equals(id1)
                        || item.getProducto().getId().equals(id2)
                        || item.getProducto().getId().equals(id3))
                .mapToDouble(CarritoItem::getSubtotal)
                .sum();

        return totalPack * porcentaje;
    }
}