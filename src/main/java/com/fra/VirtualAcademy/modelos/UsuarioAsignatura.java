package com.fra.VirtualAcademy.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "usuarios_asignaturas")
public class UsuarioAsignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Campos básicos de la entidad
    @NotBlank(message = "El curso es obligatorio")
    @Column(nullable = false)
    private String curso;

    @NotBlank(message = "El grupo es obligatorio")
    @Column(nullable = false)
    private String grupo;

    //Control de notas negativas y superiores a 10
    @Column(nullable = true)
    private Double calificacion;

    //No permite introducir una fecha futura
    @Column(nullable = true)
    private LocalDate fechaAsistencia;

    //Relación muchos a uno con la entidad Usuario
    //Clave foránea de usuario
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull(message = "El usuario es obligatorio")
    private Usuario usuario;

    //Relación muchos a uno con la entidad Asignatura
    //Clave fornánea
    @ManyToOne
    @JoinColumn(name = "id_asignatura", nullable = false)
    @NotNull(message = "La asignatura es obligatoria")
    private Asignatura asignatura;

    //Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    public Double getCalificacion() { return calificacion; }
    public void setCalificacion(Double calificacion) { this.calificacion = calificacion; }

    public LocalDate getFechaAsistencia() { return fechaAsistencia; }
    public void setFechaAsistencia(LocalDate fechaAsistencia) { this.fechaAsistencia = fechaAsistencia; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Asignatura getAsignatura() { return asignatura; }
    public void setAsignatura(Asignatura asignatura) { this.asignatura = asignatura; }
}