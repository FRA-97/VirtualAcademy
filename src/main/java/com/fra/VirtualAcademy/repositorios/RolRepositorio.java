package com.fra.VirtualAcademy.repositorios;

import com.fra.VirtualAcademy.modelos.Rol;
import com.fra.VirtualAcademy.modelos.Enumeracion.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RolRepositorio extends JpaRepository<Rol, Long> {

    //Busca un rol por su cargo
    Optional<Rol> findByCargo(Cargo cargo);
}
