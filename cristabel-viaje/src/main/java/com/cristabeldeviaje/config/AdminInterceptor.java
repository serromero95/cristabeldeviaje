package com.cristabeldeviaje.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false); // No creamos sesión si no existe

        if (session != null) {
            String rol = (String) session.getAttribute("usuario_rol");
            if ("admin".equals(rol)) {
                return true; // Es admin, adelante
            }
        }

        response.sendRedirect(request.getContextPath() + "/login"); // Si no es admin, al login
        return false;
    }
}