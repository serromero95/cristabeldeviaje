package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.DetallePedido;
import com.cristabeldeviaje.model.Pedido;
import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.model.Usuario;
import com.cristabeldeviaje.repository.PedidoRepository;
import com.cristabeldeviaje.repository.ProductoRepository;
import com.cristabeldeviaje.repository.UsuarioRepository;
import com.cristabeldeviaje.service.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CarritoService carritoService;

    @GetMapping("/carrito/finalizar")
    public String finalizarPedido(HttpSession session) {

        String email = (String) session.getAttribute("usuario_email");

        if (email == null) {
            return "redirect:/login";
        }

        var carrito = carritoService.getItems();

        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/carrito";
        }

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("pendiente");

        double totalPedido = 0;
        List<DetallePedido> detalles = new ArrayList<>();

        for (var item : carrito) {
            Producto producto = item.getProducto();
            Integer cantidad = item.getCantidad();

            Integer stockActual = producto.getStock();

            if (stockActual == null) {
                stockActual = 0;
            }

            if (stockActual < cantidad) {
                return "redirect:/carrito?error=stock";
            }

            producto.setStock(stockActual - cantidad);
            productoRepository.save(producto);

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecio(producto.getPrecio());
            detalle.setTalla(item.getTalla());
            detalle.setColor(item.getColor());

            detalles.add(detalle);
            totalPedido += producto.getPrecio() * cantidad;
        }

        pedido.setTotal(totalPedido);
        pedido.setDetalles(detalles);

        pedidoRepository.save(pedido);

        carritoService.limpiar();

        return "redirect:/mis-pedidos?success";
    }

    @GetMapping("/mis-pedidos")
    public String verMisPedidos(HttpSession session, Model model) {

        String email = (String) session.getAttribute("usuario_email");

        if (email == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            return "redirect:/login";
        }

        List<Pedido> pedidos = pedidoRepository.findByUsuario(usuario);
        model.addAttribute("pedidos", pedidos);

        return "mis-pedidos";
    }
}