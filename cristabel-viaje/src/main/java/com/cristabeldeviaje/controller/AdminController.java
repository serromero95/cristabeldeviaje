package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.repository.ProductoRepository;
import com.cristabeldeviaje.repository.CategoriaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.cristabeldeviaje.model.Pedido;
import com.cristabeldeviaje.repository.PedidoRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

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
    public String guardarProducto(@ModelAttribute Producto producto,
                                  @RequestParam("archivoImagen") MultipartFile archivoImagen,
                                  HttpSession session) {

        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equals("admin")) return "redirect:/";

        if (producto.getId() != null) {
            Producto existente = productoRepository.findById(producto.getId()).orElse(null);
            if (existente != null && (archivoImagen == null || archivoImagen.isEmpty())) {
                producto.setImagen(existente.getImagen());
            }
        }

        if (archivoImagen != null && !archivoImagen.isEmpty()) {
            try {
                String nombreOriginal = archivoImagen.getOriginalFilename();
                String extension = "";

                if (nombreOriginal != null && nombreOriginal.contains(".")) {
                    extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
                }

                String nombreArchivo = System.currentTimeMillis() + extension;

                // Leer bytes una sola vez
                byte[] bytes = archivoImagen.getBytes();

                // RUTA 1 → target
                String dirTarget = System.getProperty("user.dir") + "/cristabel-viaje/target/classes/static/img/";
                Path rutaTarget = Paths.get(dirTarget);
                Files.createDirectories(rutaTarget);
                Path archivoTarget = rutaTarget.resolve(nombreArchivo);
                Files.write(archivoTarget, bytes);

                // RUTA 2 → src
                String dirSrc = System.getProperty("user.dir") + "/cristabel-viaje/src/main/resources/static/img/";
                Path rutaSrc = Paths.get(dirSrc);
                Files.createDirectories(rutaSrc);
                Path archivoSrc = rutaSrc.resolve(nombreArchivo);
                Files.write(archivoSrc, bytes);

                producto.setImagen(nombreArchivo);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error al subir la imagen: " + e.getMessage());
            }
        }

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

    // PEDIDOS ADMIN

    // Listar pedidos
    @GetMapping("/pedidos")
    public String listarPedidos(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        model.addAttribute("pedidos", pedidoRepository.findAll());
        return "admin/pedidos";
    }

    // Actualizar estado
    @PostMapping("/pedidos/estado")
    public String actualizarEstado(@RequestParam Long id, @RequestParam String estado, HttpSession session) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido != null) {
            pedido.setEstado(estado);
            pedidoRepository.save(pedido);
        }

        return "redirect:/admin/pedidos";
    }
}