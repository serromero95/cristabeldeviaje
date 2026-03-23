package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.repository.ProductoRepository;
import com.cristabeldeviaje.service.CarritoService;
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
    public String verCarrito(Model model) {
        model.addAttribute("items", carritoService.getItems());
        model.addAttribute("total", carritoService.getTotal());
        return "carrito";
    }

    @PostMapping("/add")
    public String agregarAlCarrito(@RequestParam Long id,
                                   @RequestParam(required = false) String talla,
                                   @RequestParam(required = false) String color,
                                   @RequestParam(defaultValue = "1") Integer cantidad) {

        Producto producto = productoRepository.findById(id).orElse(null);

        if (producto != null) {
            carritoService.agregarProducto(producto, talla, color, cantidad);
        }

        return "redirect:/carrito";
    }

    @PostMapping("/actualizar")
    public String actualizarCantidad(@RequestParam String clave,
                                     @RequestParam Integer cantidad) {
        carritoService.actualizarCantidad(clave, cantidad);
        return "redirect:/carrito";
    }

    @GetMapping("/eliminar")
    public String eliminarProducto(@RequestParam String clave) {
        carritoService.eliminarProducto(clave);
        return "redirect:/carrito";
    }
}