package com.fra.VirtualAcademy.controladores;

import com.fra.VirtualAcademy.modelos.Rol;
import com.fra.VirtualAcademy.servicios.RolServicio;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//Define los endpoints CRUD básicos
@Controller
@RequestMapping("/roles")
public class RolControlador {

    private final RolServicio rolServicio;

    public RolControlador(RolServicio rolServicio) {
        this.rolServicio = rolServicio;
    }

    //Muestra todos los roles
    @GetMapping
    public String listarRoles(Model model) {
        List<Rol> roles = rolServicio.listarRoles();
        model.addAttribute("roles", roles);
        return "roles/roles";
    }

    //Redirige al formulario para crear rol
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoRol(Model model) {
        model.addAttribute("rol", new Rol());
        return "roles/formulario";
    }

    //Crea nuevo rol
    @PostMapping
    public String guardarRol(@Valid @ModelAttribute("rol") Rol rol) {
        rolServicio.guardarRol(rol);
        return "redirect:/roles";
    }

    //Obtiene rol por ID
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Rol rol = rolServicio.listarRoles().stream()
                .filter(r -> r.getIdRol().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        model.addAttribute("rol", rol);
        return "roles/formulario";
    }

    //Actualiza rol existente
    @PostMapping("/actualizar/{id}")
    public String actualizarRol(@PathVariable Long id, @Valid @ModelAttribute("rol") Rol rolActualizado) {
        rolServicio.actualizarRol(id, rolActualizado);
        return "redirect:/roles";
    }
}
