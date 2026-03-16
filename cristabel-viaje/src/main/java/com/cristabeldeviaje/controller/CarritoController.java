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
        model.addAttribute("items", carritoService.getProductos());
        model.addAttribute("total", carritoService.getTotal());
        return "carrito";
    }

    @PostMapping("/add")
    public String agregarAlCarrito(@RequestParam Long id) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            carritoService.agregarProducto(producto);
        }
        return "redirect:/carrito";
    }
}