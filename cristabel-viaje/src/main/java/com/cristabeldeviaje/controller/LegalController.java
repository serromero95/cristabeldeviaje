package com.cristabeldeviaje.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/aviso-legal")
    public String avisoLegal() {
        return "legal/aviso-legal";
    }

    @GetMapping("/privacidad")
    public String privacidad() {
        return "legal/privacidad";
    }

    @GetMapping("/cookies")
    public String cookies() {
        return "legal/cookies";
    }

    @GetMapping("/condiciones")
    public String condiciones() {
        return "legal/condiciones";
    }

    @GetMapping("/devoluciones")
    public String devoluciones() {
        return "legal/devoluciones";
    }
}