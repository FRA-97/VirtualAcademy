package com.fra.VirtualAcademy.controladores;

import com.fra.VirtualAcademy.modelos.Usuario;
import com.fra.VirtualAcademy.servicios.UsuarioServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/informacion")
public class InformacionEstudiantesControlador {

    //Clase para gestionar la pantalla de los estudiantes

    private final UsuarioServicio usuarioServicio;

    public InformacionEstudiantesControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    //Consigue la información de los estudiantes
    @GetMapping("/estudiantes")
    public String mostrarInformacionEstudiante(Model model, Principal principal) {

        //Obtiene email del usuario autenticado
        String email = principal.getName();

        //Busca ese usuario concreto
        Usuario estudiante = usuarioServicio.buscarPorEmail(email);

        //Agregar el estudiante
        model.addAttribute("estudiante", estudiante);

        return "informacion/estudiantes";
    }
}
