package com.fra.VirtualAcademy.servicios;
import com.fra.VirtualAcademy.excepciones.ExcepcionesPersonalizadas.*;
import com.fra.VirtualAcademy.modelos.Asignatura;
import com.fra.VirtualAcademy.repositorios.AsignaturaRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsignaturaServicio {

    private final AsignaturaRepositorio asignaturaRepositorio;

    public AsignaturaServicio(AsignaturaRepositorio asignaturaRepositorio) {
        this.asignaturaRepositorio = asignaturaRepositorio;
    }

    //Lista todas las asignaturas
    public List<Asignatura> listarAsignaturas() {
        return asignaturaRepositorio.findAll();
    }

    //Busca una asignatura por ID
    public Asignatura buscarPorId(Long id) {
        return asignaturaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontrado("Asignatura no encontrada con id: " + id));
    }

    //Guardar una asignatura
    public Asignatura guardarAsignatura(Asignatura asignatura) {
        boolean existe = asignaturaRepositorio.existsByNombre(asignatura.getNombre());
        //Verifica que la asignatura existe previamente
        if (existe) {
            throw new RecursoDuplicado("Ya existe una asignatura con el nombre: " + asignatura.getNombre());
        }
        return asignaturaRepositorio.save(asignatura);
    }

    //Elimina una asignatura
    public void eliminarAsignatura(Long id) {
        //Debe existir la asignatura previamente
        if (!asignaturaRepositorio.existsById(id)) {
            throw new RecursoNoEncontrado("No se puede eliminar. La asignatura con id " + id + " no existe.");
        }
        asignaturaRepositorio.deleteById(id);
    }

    //Edita una asignatura
    public Asignatura actualizarAsignatura(Long id, Asignatura asignaturaActualizada) {
        Asignatura existente = asignaturaRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontrado("Asignatura no encontrada con id: " + id));

        //Si cambia el nombre, verifica que no exista otra asignatura con ese nombre
        if (!existente.getNombre().equalsIgnoreCase(asignaturaActualizada.getNombre()) &&
                asignaturaRepositorio.existsByNombre(asignaturaActualizada.getNombre())) {
            throw new RecursoDuplicado("Ya existe una asignatura con el nombre: " + asignaturaActualizada.getNombre());
        }

        //Modifica los datos
        existente.setNombre(asignaturaActualizada.getNombre());
        existente.setDescripcion(asignaturaActualizada.getDescripcion());
        existente.setHorario(asignaturaActualizada.getHorario());

        return asignaturaRepositorio.save(existente);
    }
}
