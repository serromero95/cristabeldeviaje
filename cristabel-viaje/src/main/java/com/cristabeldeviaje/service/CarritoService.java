package com.cristabeldeviaje.service;

import com.cristabeldeviaje.model.Producto;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.Map;

@Service
@SessionScope // Esto hace que cada usuario tenga su propio carrito
public class CarritoService {

    private Map<Producto, Integer> productos = new HashMap<>();

    public void agregarProducto(Producto producto) {
        if (productos.containsKey(producto)) {
            productos.put(producto, productos.get(producto) + 1);
        } else {
            productos.put(producto, 1);
        }
    }

    public Map<Producto, Integer> getProductos() {
        return productos;
    }

    public Double getTotal() {
        return productos.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrecio() * entry.getValue())
                .sum();
    }

    public void limpiar() {
        productos.clear();
    }
}