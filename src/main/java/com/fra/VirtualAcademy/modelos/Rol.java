package com.fra.VirtualAcademy.modelos;

import com.fra.VirtualAcademy.modelos.Enumeracion.Cargo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

//Etiqueta para definir la entidad
@Entity
//Nombre de la tabla
@Table(name = "roles")
public class Rol {

    //Etiqueta para definir la clave primaria
    @Id
    //Etiqueta para la generación automática del ID
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Long idRol;

    //Etiqueta para definir la enumeración
    @Enumerated(EnumType.STRING)
    //La columna no puede ser nula y se limita a 20 caracteres
    @NotNull(message = "El cargo es obligatorio")
    @Column(nullable = false, length = 20)
    private Cargo cargo;

    //Relación uno a muchos con la entidad Usuario
    @OneToMany(mappedBy = "rol", cascade = CascadeType.ALL)
    private List<Usuario> usuarios;

    //Getters y setters
    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }
}