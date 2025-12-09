package com.fra.VirtualAcademy.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "asignaturas")
public class Asignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignatura")
    private Long idAsignatura;

    //Campos básicos de la entidad
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false)
    private String descripcion;

    @NotBlank(message = "El horario es obligatorio")
    @Column(nullable = false)
    private String horario;

    //Relación uno a muchos con la entidad UsuariosAsignatura
    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsuarioAsignatura> usuarioAsignaturas = new HashSet<>();

    //Getters y setters
    public Long getIdAsignatura() { return idAsignatura; }
    public void setIdAsignatura(Long idAsignatura) { this.idAsignatura = idAsignatura; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public Set<UsuarioAsignatura> getUsuarioAsignaturas() { return usuarioAsignaturas; }
    public void setUsuarioAsignaturas(Set<UsuarioAsignatura> usuarioAsignaturas) { this.usuarioAsignaturas = usuarioAsignaturas; }
}
