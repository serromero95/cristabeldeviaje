package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.Producto;
import com.cristabeldeviaje.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PackController {

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/packs")
    public String verPacks() {
        return "packs";
    }

    @GetMapping("/pack/principiantes")
    public String packPrincipiantes(Model model) {

        List<Producto> productos = productoRepository.findAllById(List.of(14L, 15L, 10L));

        double total = productos.stream()
                .mapToDouble(Producto::getPrecio)
                .sum();

        double precioPack = total * 0.9;

        model.addAttribute("codigoPack", "principiantes");
        model.addAttribute("nombrePack", "Pack Principiantes");
        model.addAttribute("productos", productos);
        model.addAttribute("precioOriginal", total);
        model.addAttribute("precioPack", precioPack);

        return "pack";
    }

    @GetMapping("/pack/cristabel")
    public String packCristabel(Model model) {

        List<Producto> productos = productoRepository.findAllById(List.of(12L, 7L, 13L));

        double total = productos.stream()
                .mapToDouble(Producto::getPrecio)
                .sum();

        double precioPack = total * 0.85;

        model.addAttribute("codigoPack", "cristabel");
        model.addAttribute("nombrePack", "Pack Cristabel de Viaje");
        model.addAttribute("productos", productos);
        model.addAttribute("precioOriginal", total);
        model.addAttribute("precioPack", precioPack);

        return "pack";
    }
}