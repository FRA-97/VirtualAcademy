package com.fra.VirtualAcademy.servicios;

import com.fra.VirtualAcademy.modelos.Rol;
import com.fra.VirtualAcademy.modelos.Enumeracion.Cargo;
import com.fra.VirtualAcademy.repositorios.RolRepositorio;
import com.fra.VirtualAcademy.excepciones.ExcepcionesPersonalizadas.*;
import org.springframework.stereotype.Service;
import java.util.List;

//Contiene la lógica
@Service
public class RolServicio {

    private final RolRepositorio rolRepositorio;

    public RolServicio(RolRepositorio rolRepositorio) {
        this.rolRepositorio = rolRepositorio;
    }

    //Lista todos los roles existentes
    public List<Rol> listarRoles() {
        List<Rol> roles = rolRepositorio.findAll();
        //Si no hay roles registrados, lanza una excepción personalizada
        if (roles.isEmpty()) {
            throw new RecursoNoEncontrado("No hay roles registrados en el sistema");
        }
        return roles;
    }

    //Busca un rol por su ID
    public Rol buscarPorId(Long id) {
        return rolRepositorio.findById(id)
                //Excepción personalizada por si no existe el cargo
                .orElseThrow(() -> new RecursoNoEncontrado("Rol no encontrado con ID: " + id));
    }

    //Busca un rol por cargo
    public Rol buscarPorCargo(Cargo cargo) {
        return rolRepositorio.findByCargo(cargo)
                //Excepción personalizada por si no existe el cargo
                .orElseThrow(() -> new RecursoNoEncontrado("No existe un rol con el cargo: " + cargo));
    }

    //Crea un nuevo rol
    public Rol guardarRol(Rol rol) {
        boolean existe = rolRepositorio.findByCargo(rol.getCargo()).isPresent();
        //Evita crear roles duplicados
        if (existe) {
            throw new RecursoDuplicado("Ya existe un rol con el cargo: " + rol.getCargo());
        }
        return rolRepositorio.save(rol);
    }

    //Edita un rol existente
    public Rol actualizarRol(Long id, Rol rolActualizado) {
        //Se busca el rol existente
        Rol existente = rolRepositorio.findById(id)
                //Si no se encuentra, se lanza excepción personalizada
                .orElseThrow(() -> new RecursoNoEncontrado("Rol no encontrado con ID: " + id));

        //Si se intenta cambiar el cargo a uno que ya existe, se lanza excepción
        if (!existente.getCargo().equals(rolActualizado.getCargo()) &&
                rolRepositorio.findByCargo(rolActualizado.getCargo()).isPresent()) {
            throw new RecursoDuplicado("Ya existe un rol con el cargo: " + rolActualizado.getCargo());
        }
        //Actualiza el cargo y guarda los cambios
        existente.setCargo(rolActualizado.getCargo());
        return rolRepositorio.save(existente);
    }
}
