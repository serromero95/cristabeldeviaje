package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.Usuario;
import com.cristabeldeviaje.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- REGISTRO ---
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute Usuario usuario,
                               @RequestParam String confirmPassword,
                               Model model) {

        Usuario existente = usuarioRepository.findByEmail(usuario.getEmail());

        if (existente != null) {
            model.addAttribute("error", "Ya existe una cuenta con ese email");
            return "register";
        }

        if (!usuario.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "register";
        }

        usuario.setRol("cliente");
        usuarioRepository.save(usuario);

        return "redirect:/login?success";
    }

    // --- LOGIN ---
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        Usuario user = usuarioRepository.findByEmail(email);

        if (user != null && password.equals(user.getPassword())) {
            session.setAttribute("usuario_id", user.getId());
            session.setAttribute("usuario_nombre", user.getNombre());
            session.setAttribute("usuario_rol", user.getRol());
            session.setAttribute("usuario_email", user.getEmail());

            if ("admin".equalsIgnoreCase(user.getRol())) {
                return "redirect:/admin";
            }

            return "redirect:/";
        }

        model.addAttribute("error", "Email o contraseña incorrectos");
        return "login";
    }

    // --- LOGOUT ---
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}