package com.fra.VirtualAcademy.repositorios;

import com.fra.VirtualAcademy.modelos.Enumeracion;
import com.fra.VirtualAcademy.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    //Busca un usuario por su dirección de correo electrónico
    Optional<Usuario> findByEmail(String email);

    //Busca una lista de usuarios asociados a un rol específico
    List<Usuario> findByRol_IdRol(Long idRol);

    //Verifica si ya existe un DNI
    boolean existsByDni(String dni);

    //Verifica si ya existe un teléfono
    boolean existsByTelefono(String telefono);

    //Verifica si ya existe un correo
    boolean existsByEmail(String email);

    //Busca un determinado rol
    List<Usuario> findByRol_Cargo(Enumeracion.Cargo cargo);
}
