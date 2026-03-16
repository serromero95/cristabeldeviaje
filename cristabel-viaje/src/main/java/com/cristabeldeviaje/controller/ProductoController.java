package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/")
    public String listarProductos(Model model) {
        List<Producto> productos = productoRepository.findAll();
        model.addAttribute("productos", productos);
        return "index";
    }

    @GetMapping("/producto/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            return "redirect:/";
        }
        model.addAttribute("producto", producto);
        return "detalle";
    }
}