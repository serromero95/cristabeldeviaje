package com.cristabeldeviaje.service;

import com.cristabeldeviaje.model.CarritoItem;
import com.cristabeldeviaje.model.Producto;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@SessionScope
public class CarritoService {

    private final Map<String, CarritoItem> items = new LinkedHashMap<>();

    private String generarClave(Producto producto, String talla, String color) {
        return producto.getId() + "|" +
                (talla != null ? talla.trim() : "") + "|" +
                (color != null ? color.trim() : "");
    }

    public void agregarProducto(Producto producto, String talla, String color, int cantidad) {
        if (cantidad <= 0) {
            cantidad = 1;
        }

        String clave = generarClave(producto, talla, color);

        if (items.containsKey(clave)) {
            CarritoItem itemExistente = items.get(clave);
            itemExistente.setCantidad(itemExistente.getCantidad() + cantidad);
        } else {
            CarritoItem nuevoItem = new CarritoItem(
                    producto,
                    (talla != null && !talla.isBlank()) ? talla.trim() : null,
                    (color != null && !color.isBlank()) ? color.trim() : null,
                    cantidad
            );
            items.put(clave, nuevoItem);
        }
    }

    public void actualizarCantidad(String clave, int cantidad) {
        if (!items.containsKey(clave)) {
            return;
        }

        if (cantidad <= 0) {
            items.remove(clave);
        } else {
            items.get(clave).setCantidad(cantidad);
        }
    }

    public void eliminarProducto(String clave) {
        items.remove(clave);
    }

    public Collection<CarritoItem> getItems() {
        return items.values();
    }

    public Double getTotal() {
        return items.values().stream()
                .mapToDouble(CarritoItem::getSubtotal)
                .sum();
    }

    public void limpiar() {
        items.clear();
    }
}