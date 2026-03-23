package com.cristabeldeviaje.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem implements Serializable {
    private Producto producto;
    private String talla;
    private String color;
    private Integer cantidad;

    public String getClave() {
        return producto.getId() + "|" +
                (talla != null ? talla : "") + "|" +
                (color != null ? color : "");
    }

    public Double getSubtotal() {
        return producto.getPrecio() * cantidad;
    }
}