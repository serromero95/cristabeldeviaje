package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.model.Pedido;
import com.cristabeldeviaje.model.Valoracion;
import com.cristabeldeviaje.repository.ProductoRepository;
import com.cristabeldeviaje.repository.CategoriaRepository;
import com.cristabeldeviaje.repository.PedidoRepository;
import com.cristabeldeviaje.repository.ValoracionRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ValoracionRepository valoracionRepository;

    // 🔹 PANEL ADMIN (HOME)
    @GetMapping("")
    public String panelAdmin(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");

        if (rol == null || !rol.equalsIgnoreCase("admin")) {
            return "redirect:/";
        }

        // Datos básicos (muy pro 👇)
        model.addAttribute("totalProductos", productoRepository.count());
        model.addAttribute("totalPedidos", pedidoRepository.count());
        model.addAttribute("totalValoraciones", valoracionRepository.count());

        return "admin/index";
    }

    // 🔹 PRODUCTOS

    @GetMapping("/productos")
    public String listarProductosAdmin(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        model.addAttribute("productos", productoRepository.findAll());
        return "admin/productos";
    }

    @GetMapping("/productos/nuevo")
    public String formularioNuevo(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "admin/form-producto";
    }

    @GetMapping("/productos/edit/{id}")
    public String formularioEditar(@PathVariable Long id, HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        Producto p = productoRepository.findById(id).orElse(null);
        model.addAttribute("producto", p);
        model.addAttribute("categorias", categoriaRepository.findAll());
        return "admin/form-producto";
    }

    @PostMapping("/productos/save")
    public String guardarProducto(@ModelAttribute Producto producto,
                                  @RequestParam("archivoImagen") MultipartFile archivoImagen,
                                  HttpSession session) {

        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

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
                byte[] bytes = archivoImagen.getBytes();

                String dirTarget = System.getProperty("user.dir") + "/cristabel-viaje/target/classes/static/img/";
                Path rutaTarget = Paths.get(dirTarget);
                Files.createDirectories(rutaTarget);
                Files.write(rutaTarget.resolve(nombreArchivo), bytes);

                String dirSrc = System.getProperty("user.dir") + "/cristabel-viaje/src/main/resources/static/img/";
                Path rutaSrc = Paths.get(dirSrc);
                Files.createDirectories(rutaSrc);
                Files.write(rutaSrc.resolve(nombreArchivo), bytes);

                producto.setImagen(nombreArchivo);

            } catch (Exception e) {
                throw new RuntimeException("Error al subir imagen");
            }
        }

        productoRepository.save(producto);
        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/delete/{id}")
    public String eliminarProducto(@PathVariable Long id, HttpSession session) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol != null && rol.equalsIgnoreCase("admin")) {
            productoRepository.deleteById(id);
        }
        return "redirect:/admin/productos";
    }

    // 🔹 PEDIDOS

    @GetMapping("/pedidos")
    public String listarPedidos(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        model.addAttribute("pedidos", pedidoRepository.findAllByOrderByFechaDesc());
        return "admin/pedidos";
    }

    @PostMapping("/pedidos/estado")
    public String actualizarEstado(@RequestParam Long id,
                                   @RequestParam String estado,
                                   HttpSession session) {

        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        Pedido pedido = pedidoRepository.findById(id).orElse(null);

        if (pedido != null) {
            pedido.setEstado(estado);
            pedidoRepository.save(pedido);
        }

        return "redirect:/admin/pedidos";
    }

    // 🔹 VALORACIONES

    @GetMapping("/valoraciones")
    public String listarValoraciones(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        model.addAttribute("valoraciones", valoracionRepository.findAll());
        return "admin/valoraciones";
    }

    @PostMapping("/valoraciones/responder")
    public String responderValoracion(@RequestParam Long id,
                                      @RequestParam String respuestaAdmin,
                                      HttpSession session) {

        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        Valoracion valoracion = valoracionRepository.findById(id).orElse(null);

        if (valoracion != null) {
            valoracion.setRespuestaAdmin(respuestaAdmin);
            valoracion.setFechaRespuesta(LocalDateTime.now());
            valoracionRepository.save(valoracion);
        }

        return "redirect:/admin/valoraciones";
    }

    // CATEGORÍAS

    @GetMapping("/categorias")
    public String listarCategorias(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        var categorias = categoriaRepository.findAll();

        // mapa: categoriaId -> nº productos
        Map<Long, Long> conteo = new HashMap<>();

        for (var c : categorias) {
            conteo.put(c.getId(), productoRepository.countByCategoria(c));
        }

        model.addAttribute("categorias", categorias);
        model.addAttribute("conteo", conteo);

        return "admin/categorias";
    }

    @GetMapping("/categorias/nuevo")
    public String nuevaCategoria(HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        model.addAttribute("categoria", new com.cristabeldeviaje.model.Categoria());
        return "admin/form-categoria";
    }

    @GetMapping("/categorias/edit/{id}")
    public String editarCategoria(@PathVariable Long id, HttpSession session, Model model) {
        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        var categoria = categoriaRepository.findById(id).orElse(null);
        model.addAttribute("categoria", categoria);
        return "admin/form-categoria";
    }

    @PostMapping("/categorias/save")
    public String guardarCategoria(@ModelAttribute com.cristabeldeviaje.model.Categoria categoria,
                                   HttpSession session) {

        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        categoriaRepository.save(categoria);
        return "redirect:/admin/categorias";
    }

    @GetMapping("/categorias/delete/{id}")
    public String eliminarCategoria(@PathVariable Long id, HttpSession session) {

        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || !rol.equalsIgnoreCase("admin")) return "redirect:/";

        var categoria = categoriaRepository.findById(id).orElse(null);

        if (categoria != null) {
            long total = productoRepository.countByCategoria(categoria);

            if (total == 0) {
                categoriaRepository.deleteById(id);
                return "redirect:/admin/categorias?eliminado";
            } else {
                return "redirect:/admin/categorias?errorCategoria";
            }
        }

        return "redirect:/admin/categorias";
    }
}