package com.fra.VirtualAcademy.controladores;

import com.fra.VirtualAcademy.excepciones.ExcepcionesPersonalizadas.*;
import com.fra.VirtualAcademy.modelos.Usuario;
import com.fra.VirtualAcademy.servicios.RolServicio;

import com.fra.VirtualAcademy.servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioControlador {

    private final UsuarioServicio usuarioServicio;
    private final RolServicio rolServicio;

    public UsuarioControlador(UsuarioServicio usuarioServicio, RolServicio rolServicio) {
        this.usuarioServicio = usuarioServicio;
        this.rolServicio = rolServicio;
    }

    //Muestra lista de usuarios
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioServicio.listarUsuarios());
        return "usuarios/usuarios";
    }

    //Redirige al formulario para crear un nuevo usuario
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", rolServicio.listarRoles());
        return "usuarios/formulario";
    }

    //Crea un nuevo usuario
    @PostMapping
    public String guardarUsuario(@Valid @ModelAttribute("usuario") Usuario usuario,
                                 Model model) {
        try {
            usuarioServicio.guardarUsuario(usuario);
            return "redirect:/usuarios";

        } catch (RecursoDuplicado e) {
            if (e.getMessage().contains("correo"))
                model.addAttribute("errorEmail", e.getMessage());
            if (e.getMessage().contains("DNI"))
                model.addAttribute("errorDni", e.getMessage());
            if (e.getMessage().contains("teléfono"))
                model.addAttribute("errorTelefono", e.getMessage());
        }

        model.addAttribute("roles", rolServicio.listarRoles());
        return "usuarios/formulario";
    }

    //Redirige a la pantalla para editar un usuario
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioServicio.buscarPorId(id));
        model.addAttribute("roles", rolServicio.listarRoles());
        return "usuarios/formulario";
    }

    //Edita un usuario existente
    @PostMapping("/actualizar/{id}")
    public String actualizarUsuario(@PathVariable Long id,
                                    @ModelAttribute("usuario") Usuario usuarioActualizado,
                                    Model model) {
        try {
            Usuario original = usuarioServicio.buscarPorId(id);
            //Mantiene la contraseña si no se ha introducido una nueva
            if (usuarioActualizado.getContraseña() == null ||
                    usuarioActualizado.getContraseña().isBlank()) {
                usuarioActualizado.setContraseña(original.getContraseña());
            }

            usuarioServicio.actualizarUsuario(id, usuarioActualizado);
            return "redirect:/usuarios";

        } catch (RecursoDuplicado e) {
            if (e.getMessage().contains("correo"))
                model.addAttribute("errorEmail", e.getMessage());
            if (e.getMessage().contains("DNI"))
                model.addAttribute("errorDni", e.getMessage());
            if (e.getMessage().contains("teléfono"))
                model.addAttribute("errorTelefono", e.getMessage());
        }

        model.addAttribute("roles", rolServicio.listarRoles());
        return "usuarios/formulario";
    }

    //Redirige a la pantalla para ver los detalles de un usuario
    @GetMapping("/{id}")
    public String verDetalleUsuario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioServicio.buscarPorId(id));
        return "usuarios/detalles";
    }

    //Elimina un usuario
    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id) {
        usuarioServicio.eliminarUsuario(id);
        return "redirect:/usuarios";
    }
}
