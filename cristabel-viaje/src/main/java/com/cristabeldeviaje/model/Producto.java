package com.cristabeldeviaje.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    private Double precio;

    private Integer stock; // Campo nuevo para el CRUD de admin

    private String imagen;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}