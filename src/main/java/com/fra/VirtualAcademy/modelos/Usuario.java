package com.fra.VirtualAcademy.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    //Campos de la entidad
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Column(nullable = false)
    private String apellidos;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Column(nullable = false, unique = true)
    private String email;

    //Contraseña con longitud determinada
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
            message = "La contraseña debe contener al menos una letra mayúscula y un número"
    )
    @Column(nullable = false)
    private String contraseña;

    //DNI con formato específico
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^[0-9]{8}[A-Za-z]$", message = "El DNI debe tener 8 números seguidos de una letra")
    @Column(nullable = false, unique = true, length = 9)
    private String dni;

    //Teléfono con regla concreta
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9}$", message = "El teléfono debe tener 9 dígitos")
    @Column(nullable = false, length = 9)
    private String telefono;

    //Relación muchos a unos con la entidad Rol
    //Cada usuario pertenece a un único rol
    //Clave foránea
    @ManyToOne
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    //Relación uno a muchos con la entidad UsuarioAsignatura
    //Si se elimina un usuario, también se eliminan el resto de registros
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsuarioAsignatura> usuarioAsignaturas = new HashSet<>();

    //Getters y setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Set<UsuarioAsignatura> getUsuarioAsignaturas() {
        return usuarioAsignaturas;
    }

    public void setUsuarioAsignaturas(Set<UsuarioAsignatura> usuarioAsignaturas) {
        this.usuarioAsignaturas = usuarioAsignaturas;
    }
}