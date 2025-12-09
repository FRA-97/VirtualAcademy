package com.fra.VirtualAcademy.controladores;

import com.fra.VirtualAcademy.excepciones.ExcepcionesPersonalizadas;
import com.fra.VirtualAcademy.modelos.Asignatura;
import com.fra.VirtualAcademy.modelos.Usuario;
import com.fra.VirtualAcademy.modelos.UsuarioAsignatura;
import com.fra.VirtualAcademy.modelos.Enumeracion;
import com.fra.VirtualAcademy.servicios.UsuarioAsignaturaServicio;
import com.fra.VirtualAcademy.servicios.UsuarioServicio;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/usuario-asignatura")
public class UsuarioAsignaturaControlador {

    private final UsuarioAsignaturaServicio usuarioAsignaturaServicio;
    private final UsuarioServicio usuarioServicio;

    public UsuarioAsignaturaControlador(UsuarioAsignaturaServicio usuarioAsignaturaServicio,
                                        UsuarioServicio usuarioServicio) {
        this.usuarioAsignaturaServicio = usuarioAsignaturaServicio;
        this.usuarioServicio = usuarioServicio;
    }

    //Muestra todas las relaciones
    @GetMapping
    public String listarTodas(Model model, Authentication authentication) {

        List<UsuarioAsignatura> relaciones = usuarioAsignaturaServicio.listarTodas();

        //Obtiene usuario logueado
        Usuario usuarioLogueado = usuarioServicio.buscarPorEmail(authentication.getName());

        //Si es profesor, filtra solo estudiantes
        if (usuarioLogueado.getRol() != null &&
                usuarioLogueado.getRol().getCargo() == Enumeracion.Cargo.PROFESOR) {

            relaciones = relaciones.stream()
                    .filter(rel -> rel.getUsuario() != null &&
                            rel.getUsuario().getRol() != null &&
                            rel.getUsuario().getRol().getCargo() == Enumeracion.Cargo.ESTUDIANTE)
                    .toList();
        }

        model.addAttribute("relaciones", relaciones);
        return "usuarioAsignatura/listado";
    }

    //Redirije al formulario para crear una nueva relación
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevaRelacion(Model model) {
        model.addAttribute("usuarioAsignatura", new UsuarioAsignatura());
        cargarListas(model);
        return "usuarioAsignatura/formulario";
    }

    //Redirije al formulario para editar una nueva relación
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        UsuarioAsignatura relacion = usuarioAsignaturaServicio.listarTodas().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));
        model.addAttribute("usuarioAsignatura", relacion);
        cargarListas(model);
        return "usuarioAsignatura/formulario";
    }

    //Crea una nueva relación
    @PostMapping
    public String guardarRelacion(@Valid @ModelAttribute("usuarioAsignatura") UsuarioAsignatura datos,
                                  BindingResult result, Model model) {

        //Validación condicional para profesores
        if (datos.getUsuario() != null && datos.getUsuario().getRol() != null &&
                datos.getUsuario().getRol().getCargo() == Enumeracion.Cargo.PROFESOR) {
            result.getFieldErrors().removeIf(f -> f.getField().equals("calificacion") ||
                    f.getField().equals("fechaAsistencia"));
        }

        if (result.hasErrors()) {
            cargarListas(model);
            return "usuarioAsignatura/formulario";
        }

        try {
            usuarioAsignaturaServicio.guardarNuevaRelacion(datos);
        } catch (ExcepcionesPersonalizadas.RecursoDuplicado e) {
            model.addAttribute("mensaje", e.getMessage());
            cargarListas(model);
            return "usuarioAsignatura/formulario";
        }

        return "redirect:/usuario-asignatura";
    }

    // Actualizar relación existente
    @PostMapping("/actualizar/{id}")
    public String actualizarRelacion(@PathVariable Long id,
                                     @Valid @ModelAttribute("usuarioAsignatura") UsuarioAsignatura datos,
                                     BindingResult result, Model model) {

        if (datos.getUsuario() != null && datos.getUsuario().getRol() != null &&
                datos.getUsuario().getRol().getCargo() == Enumeracion.Cargo.PROFESOR) {
            result.getFieldErrors().removeIf(f -> f.getField().equals("calificacion") ||
                    f.getField().equals("fechaAsistencia"));
        }

        if (result.hasErrors()) {
            cargarListas(model);
            return "usuarioAsignatura/formulario";
        }

        usuarioAsignaturaServicio.actualizarRelacion(id, datos);
        return "redirect:/usuario-asignatura";
    }

    //Elimina una relación
    @GetMapping("/eliminar/{id}")
    public String eliminarRelacion(@PathVariable Long id) {
        usuarioAsignaturaServicio.eliminarRelacion(id);
        return "redirect:/usuario-asignatura";
    }

    //Carga lista de usuarios y asignaturas
    @ModelAttribute
    public void cargarListas(Model model) {
        List<?> usuariosFiltrados = usuarioAsignaturaServicio.listarUsuarios().stream()
                .filter(u -> u.getRol() != null && u.getRol().getCargo() != Enumeracion.Cargo.SECRETARIO)
                .toList();
        model.addAttribute("usuarios", usuariosFiltrados);
        model.addAttribute("asignaturas", usuarioAsignaturaServicio.listarAsignaturas());
    }

    //Redirige a la pantalla para el filtro de usuarios
    @GetMapping("/filtrar")
    public String mostrarFiltro(Model model) {
        model.addAttribute("usuarios", usuarioAsignaturaServicio.listarUsuarios());
        return "usuarioAsignatura/filtro-usuario-asignatura";
    }

    //Redirige a la pantalla para el filtro de usuarios
    @GetMapping("/filtrar/resultado")
    public String resultadoFiltro(@RequestParam Long idUsuario, Model model) {

        Usuario usuario = usuarioAsignaturaServicio.obtenerUsuarioPorId(idUsuario);
        List<UsuarioAsignatura> relaciones = usuarioAsignaturaServicio.listarPorUsuario(idUsuario);

        model.addAttribute("usuario", usuario);
        model.addAttribute("relaciones", relaciones);

        return "usuarioAsignatura/resultado-filtro-usuario-asignatura";
    }

    //Redirige a la pantalla para el filtro de asignaturas
    @GetMapping("/filtrar/asignatura")
    public String filtrarPorAsignatura(Model model) {
        model.addAttribute("asignaturas", usuarioAsignaturaServicio.listarAsignaturas());
        return "usuarioAsignatura/filtro-asignatura";
    }

    //Redirige a la pantalla para el filtro de asignaturas
    @GetMapping("/filtrar/asignatura/resultado")
    public String resultadoFiltroAsignatura(@RequestParam Long idAsignatura, Model model) {
        //Obtiene asignatura
        Asignatura asignatura = usuarioAsignaturaServicio.obtenerAsignaturaPorId(idAsignatura);

        //Obtiene usuarios asignados a esa asignatura
        List<UsuarioAsignatura> relaciones = usuarioAsignaturaServicio.listarPorAsignatura(idAsignatura);

        model.addAttribute("asignatura", asignatura);
        model.addAttribute("relaciones", relaciones);

        return "usuarioAsignatura/resultado-filtro-asignatura";
    }

    //Redirige a la pantalla para el filtro de curso
    @GetMapping("/filtrar-curso")
    public String mostrarFiltroCurso(Model model) {
        List<String> cursos = usuarioAsignaturaServicio.listarCursosDistintos();
        model.addAttribute("cursos", cursos);
        return "usuarioAsignatura/filtro-curso";
    }

    //Redirige a la pantalla para el filtro de curso
    @GetMapping("/filtrar-curso/resultado")
    public String resultadoFiltroCurso(@RequestParam String curso, Model model) {
        List<UsuarioAsignatura> relaciones = usuarioAsignaturaServicio.listarPorCurso(curso);
        model.addAttribute("curso", curso);
        model.addAttribute("relaciones", relaciones);
        return "usuarioAsignatura/resultado-filtro-curso";
    }

    //Redirige a la pantalla para el filtro de grupo
    @GetMapping("/filtrar-grupo")
    public String filtrarPorGrupo(Model model) {
        // Obtener todos los grupos distintos existentes
        List<String> grupos = usuarioAsignaturaServicio.listarTodosGrupos();
        model.addAttribute("grupos", grupos);
        return "usuarioAsignatura/filtro-grupo-usuario-asignatura";
    }

    //Redirige a la pantalla para el filtro de grupo
    @GetMapping("/filtrar-grupo/resultado")
    public String resultadoFiltroPorGrupo(@RequestParam String grupo, Model model) {
        List<UsuarioAsignatura> relaciones = usuarioAsignaturaServicio.listarPorGrupo(grupo);
        model.addAttribute("grupo", grupo);
        model.addAttribute("relaciones", relaciones);
        return "usuarioAsignatura/resultado-filtro-grupo";
    }

}
