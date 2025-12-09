package com.fra.VirtualAcademy.repositorios;

import com.fra.VirtualAcademy.modelos.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignaturaRepositorio extends JpaRepository<Asignatura, Long> {

    //Busca una asignatura por su nombre exacto
    Asignatura findByNombre(String nombre);

    //Verifica si existe una asignatura registrada con un nombre específico
    boolean existsByNombre(String nombre);
}

