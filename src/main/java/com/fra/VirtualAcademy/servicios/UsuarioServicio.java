package com.fra.VirtualAcademy.servicios;

import com.fra.VirtualAcademy.modelos.Enumeracion;
import com.fra.VirtualAcademy.modelos.Usuario;
import com.fra.VirtualAcademy.repositorios.UsuarioRepositorio;
import com.fra.VirtualAcademy.excepciones.ExcepcionesPersonalizadas.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServicio(UsuarioRepositorio usuarioRepositorio,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    //Muestra todos los usuarios
    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    //Busca un usuario por ID
    public Usuario buscarPorId(Long id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontrado("Usuario no encontrado con ID: " + id));
    }

    //Busca un usuario por correo
    public Usuario buscarPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontrado("No existe un usuario con el correo: " + email));
    }

    //Guarda un nuevo usuario
    public Usuario guardarUsuario(Usuario usuario) {

        if (usuarioRepositorio.existsByEmail(usuario.getEmail())) {
            throw new RecursoDuplicado("El correo electrónico ya está registrado");
        }
        if (usuarioRepositorio.existsByDni(usuario.getDni())) {
            throw new RecursoDuplicado("El DNI ya está registrado");
        }
        if (usuarioRepositorio.existsByTelefono(usuario.getTelefono())) {
            throw new RecursoDuplicado("El teléfono ya está registrado");
        }
        usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        return usuarioRepositorio.save(usuario);
    }


    //Elimina un usuario
    public void eliminarUsuario(Long id) {
        if (!usuarioRepositorio.existsById(id)) {
            throw new RecursoNoEncontrado("No se puede eliminar. El usuario con ID " + id + " no existe");
        }
        usuarioRepositorio.deleteById(id);
    }

    //Edita un usuario
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {

        Usuario usuarioExistente = usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontrado("Usuario no encontrado con ID: " + id));

        //Normaliza valores antes de comparar
        String emailOriginal = usuarioExistente.getEmail().trim().toLowerCase();
        String emailNuevo = usuarioActualizado.getEmail().trim().toLowerCase();
        String dniOriginal = usuarioExistente.getDni().trim().toUpperCase();
        String dniNuevo = usuarioActualizado.getDni().trim().toUpperCase();
        String telOriginal = usuarioExistente.getTelefono().trim();
        String telNuevo = usuarioActualizado.getTelefono().trim();

        //Valida duplicado de email
        if (!emailOriginal.equals(emailNuevo) && usuarioRepositorio.existsByEmail(emailNuevo)) {
            throw new RecursoDuplicado("El correo electrónico ya está registrado");
        }
        //Valida duplicado de DNI
        if (!dniOriginal.equals(dniNuevo) && usuarioRepositorio.existsByDni(dniNuevo)) {
            throw new RecursoDuplicado("El DNI ya está registrado");
        }
        //Valida duplicado de teléfono
        if (!telOriginal.equals(telNuevo) && usuarioRepositorio.existsByTelefono(telNuevo)) {
            throw new RecursoDuplicado("El teléfono ya está registrado");
        }

        //Actualiza los datos
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellidos(usuarioActualizado.getApellidos());
        usuarioExistente.setEmail(emailNuevo);
        usuarioExistente.setDni(dniNuevo);
        usuarioExistente.setTelefono(telNuevo);
        usuarioExistente.setRol(usuarioActualizado.getRol());

        //Mantiene la contraseña si no se ha cambiado
        if (usuarioActualizado.getContraseña() != null && !usuarioActualizado.getContraseña().isBlank()) {
            usuarioExistente.setContraseña(passwordEncoder.encode(usuarioActualizado.getContraseña()));
        }

        return usuarioRepositorio.save(usuarioExistente);
    }
}
