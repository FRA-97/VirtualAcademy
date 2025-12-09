package com.fra.VirtualAcademy.repositorios;

import com.fra.VirtualAcademy.modelos.UsuarioAsignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsuarioAsignaturaRepositorio extends JpaRepository<UsuarioAsignatura, Long> {

    //Obtiene todas las relaciones Usuario-Asignatura asociadas a un usuario específico
    List<UsuarioAsignatura> findByUsuarioIdUsuario(Long idUsuario);

    //Obtiene todas las relaciones Usuario-Asignatura asociadas a una asignatura concreta
    List<UsuarioAsignatura> findByAsignaturaIdAsignatura(Long asignaturaId);

    //Obtiene todos los cursos distintos
    @Query("SELECT DISTINCT ua.curso FROM UsuarioAsignatura ua")
    List<String> findDistinctCursos();

    //Obtiene todas las relaciones Usuario-Asignatura asociadas a un curso específico
    List<UsuarioAsignatura> findByCurso(String curso);

    //Obtiene todas las relaciones Usuario-Asignatura asociadas a un grupo específico
    List<UsuarioAsignatura> findByGrupo(String grupo);
}