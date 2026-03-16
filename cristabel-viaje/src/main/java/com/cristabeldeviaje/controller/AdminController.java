package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.repository.ProductoRepository;
import com.cristabeldeviaje.repository.CategoriaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // 1. READ: Listado de productos
    @GetMapping("/productos")
    public String listarProductosAdmin(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        model.addAttribute("productos", productoRepository.findAll());
        return "admin/productos";
    }

    // 2. CREATE: Formulario para nuevo producto
    @GetMapping("/productos/nuevo")
    public String formularioNuevo(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equals("admin")) return "redirect:/";

        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "admin/form-producto";
    }

    // 3. UPDATE: Formulario para editar producto existente
    @GetMapping("/productos/edit/{id}")
    public String formularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equals("admin")) return "redirect:/";

        Producto p = productoRepository.findById(id).orElse(null);
        model.addAttribute("producto", p);
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "admin/form-producto";
    }

    // PROCESAR GUARDADO (Sirve tanto para Crear como para Editar)
    @PostMapping("/productos/save")
    public String guardarProducto(@ModelAttribute Producto producto, HttpSession session) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equals("admin")) return "redirect:/";

        productoRepository.save(producto);
        return "redirect:/admin/productos";
    }

    // 4. DELETE: Eliminar producto
    @GetMapping("/productos/delete/{id}")
    public String eliminarProducto(@PathVariable Long id, HttpSession session) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol != null && rol.equalsIgnoreCase("admin")) {
            productoRepository.deleteById(id);
        }
        return "redirect:/admin/productos";
    }
}