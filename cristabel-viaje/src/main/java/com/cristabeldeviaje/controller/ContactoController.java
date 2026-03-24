package com.cristabeldeviaje.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactoController {

    @GetMapping("/contacto")
    public String verContacto() {
        return "contacto";
    }

    @PostMapping("/contacto")
    public String enviarContacto(@RequestParam String nombre,
                                 @RequestParam String email,
                                 @RequestParam String mensaje,
                                 Model model) {

        model.addAttribute("ok", "Tu mensaje se ha enviado correctamente. Te responderemos lo antes posible.");
        return "contacto";
    }
}