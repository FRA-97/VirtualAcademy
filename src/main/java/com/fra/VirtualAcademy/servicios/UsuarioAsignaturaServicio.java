package com.fra.VirtualAcademy.servicios;

import com.fra.VirtualAcademy.modelos.UsuarioAsignatura;
import com.fra.VirtualAcademy.modelos.Usuario;
import com.fra.VirtualAcademy.modelos.Asignatura;
import com.fra.VirtualAcademy.repositorios.AsignaturaRepositorio;
import com.fra.VirtualAcademy.repositorios.UsuarioAsignaturaRepositorio;
import com.fra.VirtualAcademy.repositorios.UsuarioRepositorio;
import com.fra.VirtualAcademy.excepciones.ExcepcionesPersonalizadas.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioAsignaturaServicio {

    private final UsuarioAsignaturaRepositorio usuarioAsignaturaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final AsignaturaRepositorio asignaturaRepositorio;

    public UsuarioAsignaturaServicio(UsuarioAsignaturaRepositorio usuarioAsignaturaRepositorio,
                                     UsuarioRepositorio usuarioRepositorio,
                                     AsignaturaRepositorio asignaturaRepositorio) {
        this.usuarioAsignaturaRepositorio = usuarioAsignaturaRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
        this.asignaturaRepositorio = asignaturaRepositorio;
    }

    //Muestra todas las relaciones
    public List<UsuarioAsignatura> listarTodas() {
        return usuarioAsignaturaRepositorio.findAll();
    }

    //Muestra los usuarios
    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    //Muestra las asignaturas
    public List<Asignatura> listarAsignaturas() {
        return asignaturaRepositorio.findAll();
    }

    //Obtiene una relación por ID
    public UsuarioAsignatura obtenerPorId(Long id) {
        return usuarioAsignaturaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontrado("Relación no encontrada con ID: " + id));
    }

    //Ordena por usuario
    public List<UsuarioAsignatura> listarPorUsuario(Long usuarioId) {
        if (!usuarioRepositorio.existsById(usuarioId)) {
            throw new RecursoNoEncontrado("Usuario no encontrado con ID: " + usuarioId);
        }
        return usuarioAsignaturaRepositorio.findByUsuarioIdUsuario(usuarioId);
    }

    //Ordena por asignatura
    public List<UsuarioAsignatura> listarPorAsignatura(Long asignaturaId) {
        if (!asignaturaRepositorio.existsById(asignaturaId)) {
            throw new RecursoNoEncontrado("Asignatura no encontrada con ID: " + asignaturaId);
        }
        return usuarioAsignaturaRepositorio.findByAsignaturaIdAsignatura(asignaturaId);
    }

    //Actualiza una relación
    public UsuarioAsignatura actualizarRelacion(Long id, UsuarioAsignatura datos) {

        UsuarioAsignatura relacionExistente = obtenerPorId(id);

        if (datos.getCurso() == null || datos.getGrupo() == null) {
            throw new DatosInvalidos("Curso y grupo son obligatorios para actualizar la relación");
        }
        relacionExistente.setCurso(datos.getCurso());
        relacionExistente.setGrupo(datos.getGrupo());
        relacionExistente.setCalificacion(datos.getCalificacion());
        relacionExistente.setFechaAsistencia(datos.getFechaAsistencia());

        return usuarioAsignaturaRepositorio.save(relacionExistente);
    }

    //Elimina una relación
    public void eliminarRelacion(Long id) {
        if (!usuarioAsignaturaRepositorio.existsById(id)) {
            throw new RecursoNoEncontrado("Relación no encontrada con ID: " + id);
        }
        usuarioAsignaturaRepositorio.deleteById(id);
    }

    //Crea una nueva relación
    public UsuarioAsignatura guardarNuevaRelacion(UsuarioAsignatura datos) {

        Usuario usuario = usuarioRepositorio.findById(datos.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontrado("Usuario no encontrado"));

        Asignatura asignatura = asignaturaRepositorio.findById(datos.getAsignatura().getIdAsignatura())
                .orElseThrow(() -> new RecursoNoEncontrado("Asignatura no encontrada"));

        //Verificar si ya existe la relación
        boolean existeRelacion = usuarioAsignaturaRepositorio
                .findByUsuarioIdUsuario(usuario.getIdUsuario())
                .stream()
                .anyMatch(rel -> rel.getAsignatura().getIdAsignatura().equals(asignatura.getIdAsignatura()));

        if (existeRelacion) {
            throw new RecursoDuplicado("El usuario ya tiene asignada esta asignatura");
        }
        datos.setUsuario(usuario);
        datos.setAsignatura(asignatura);

        return usuarioAsignaturaRepositorio.save(datos);
    }

    //Obtiene un usuario por su ID
    public Usuario obtenerUsuarioPorId(Long idUsuario) {
        return usuarioRepositorio.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontrado("Usuario no encontrado con ID: " + idUsuario));
    }

    //Obtiene una asignatura por su ID
    public Asignatura obtenerAsignaturaPorId(Long idAsignatura) {
        return asignaturaRepositorio.findById(idAsignatura)
                .orElseThrow(() -> new RecursoNoEncontrado("Asignatura no encontrada con ID: " + idAsignatura));
    }

    //Muestra los cursos distintos
    public List<String> listarCursosDistintos() {
        return usuarioAsignaturaRepositorio.findDistinctCursos();
    }

    //Muestra las relaciones por un curso determinado
    public List<UsuarioAsignatura> listarPorCurso(String curso) {
        return usuarioAsignaturaRepositorio.findByCurso(curso);
    }

    //Muestra todos los grupos
    public List<String> listarTodosGrupos() {
        return usuarioAsignaturaRepositorio.findAll()
                .stream()
                .map(UsuarioAsignatura::getGrupo)
                .distinct()
                .toList();
    }

    //Muestra las relaciones por un grupo determinado
    public List<UsuarioAsignatura> listarPorGrupo(String grupo) {
        return usuarioAsignaturaRepositorio.findByGrupo(grupo);
    }
}
