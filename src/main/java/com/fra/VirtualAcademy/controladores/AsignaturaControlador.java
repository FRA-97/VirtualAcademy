package com.fra.VirtualAcademy.controladores;

import com.fra.VirtualAcademy.modelos.Asignatura;
import com.fra.VirtualAcademy.servicios.AsignaturaServicio;
import com.fra.VirtualAcademy.excepciones.ExcepcionesPersonalizadas.RecursoDuplicado;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/asignaturas")
public class AsignaturaControlador {

    private final AsignaturaServicio asignaturaServicio;

    public AsignaturaControlador(AsignaturaServicio asignaturaServicio) {
        this.asignaturaServicio = asignaturaServicio;
    }

    //Consigue el listado de asignaturas
    @GetMapping
    public String listarAsignaturas(Model model) {
        List<Asignatura> asignaturas = asignaturaServicio.listarAsignaturas();
        model.addAttribute("asignaturas", asignaturas);
        return "asignaturas/asignaturas";
    }

    //Redirige al formulario para crear una nueva asignatura
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevaAsignatura(Model model) {
        model.addAttribute("asignatura", new Asignatura());
        return "asignaturas/formulario";
    }

    //Guarda una nueva asignatura
    @PostMapping
    public String guardarAsignatura(@Valid @ModelAttribute("asignatura") Asignatura asignatura,
                                    Model model) {
        try {
            asignaturaServicio.guardarAsignatura(asignatura);
            return "redirect:/asignaturas";

        } catch (RecursoDuplicado ex) {
            model.addAttribute("errorNombre", ex.getMessage());
            return "asignaturas/formulario";
        }
    }

    //Redirige al formulario para editar una nueva asignatura
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Asignatura asignatura = asignaturaServicio.buscarPorId(id);
        model.addAttribute("asignatura", asignatura);
        return "asignaturas/formulario";
    }

    //Edita una asignatura
    @PostMapping("/actualizar/{id}")
    public String actualizarAsignatura(@PathVariable Long id,
                                       @Valid @ModelAttribute("asignatura") Asignatura asignaturaActualizada,
                                       Model model) {
        try {
            asignaturaServicio.actualizarAsignatura(id, asignaturaActualizada);
            return "redirect:/asignaturas";

        } catch (RecursoDuplicado ex) {
            model.addAttribute("errorNombre", ex.getMessage());
            return "asignaturas/formulario";
        }
    }

    //Redirige a la pantalla para ver los detalles de cada asignatura
    @GetMapping("/{id}")
    public String verDetalleAsignatura(@PathVariable Long id, Model model) {
        Asignatura asignatura = asignaturaServicio.buscarPorId(id);
        model.addAttribute("asignatura", asignatura);
        return "asignaturas/detalles";
    }

    //Elimina una asignatura
    @GetMapping("/eliminar/{id}")
    public String eliminarAsignatura(@PathVariable Long id) {
        asignaturaServicio.eliminarAsignatura(id);
        return "redirect:/asignaturas";
    }
}
