package com.cristabeldeviaje.controller;

import com.cristabeldeviaje.model.Usuario;
import com.cristabeldeviaje.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 🔹 VER PERFIL
    @GetMapping("/perfil")
    public String verPerfil(HttpSession session, Model model) {

        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || rol.equals("admin")) {
            return "redirect:/";
        }

        Long id = (Long) session.getAttribute("usuario_id");
        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        model.addAttribute("usuario", usuario);

        return "perfil";
    }

    // 🔹 ACTUALIZAR DATOS
    @PostMapping("/perfil/update")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioForm,
                                   HttpSession session) {

        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || rol.equals("admin")) return "redirect:/";

        Long id = (Long) session.getAttribute("usuario_id");
        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario != null) {
            usuario.setNombre(usuarioForm.getNombre());
            usuario.setApellidos(usuarioForm.getApellidos());
            usuario.setDireccion(usuarioForm.getDireccion());
            usuario.setTelefono(usuarioForm.getTelefono());
            usuario.setComunidad(usuarioForm.getComunidad());
            usuario.setPais(usuarioForm.getPais());

            usuarioRepository.save(usuario);

            session.setAttribute("usuario_nombre", usuario.getNombre());
        }

        return "redirect:/perfil?ok";
    }

    // 🔹 CAMBIAR PASSWORD
    @PostMapping("/perfil/password")
    public String cambiarPassword(@RequestParam String actual,
                                  @RequestParam String nueva,
                                  @RequestParam String confirm,
                                  HttpSession session) {

        String rol = (String) session.getAttribute("usuario_rol");
        if (rol == null || rol.equals("admin")) return "redirect:/";

        Long id = (Long) session.getAttribute("usuario_id");
        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) return "redirect:/perfil?passError";

        if (!usuario.getPassword().equals(actual)) {
            return "redirect:/perfil?passError";
        }

        if (!nueva.equals(confirm)) {
            return "redirect:/perfil?passError";
        }

        usuario.setPassword(nueva);
        usuarioRepository.save(usuario);

        return "redirect:/perfil?passOk";
    }
}