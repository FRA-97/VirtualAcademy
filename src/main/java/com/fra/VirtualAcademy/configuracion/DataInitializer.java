package com.fra.VirtualAcademy.configuracion;

import com.fra.VirtualAcademy.modelos.Enumeracion.Cargo;
import com.fra.VirtualAcademy.modelos.Rol;
import com.fra.VirtualAcademy.modelos.Usuario;
import com.fra.VirtualAcademy.servicios.RolServicio;
import com.fra.VirtualAcademy.servicios.UsuarioServicio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//Crea un usuario configurado por defecto
@Component
public class DataInitializer implements CommandLineRunner {

    private final RolServicio rolServicio;
    private final UsuarioServicio usuarioServicio;

    public DataInitializer(RolServicio rolServicio, UsuarioServicio usuarioServicio) {
        this.rolServicio = rolServicio;
        this.usuarioServicio = usuarioServicio;
    }

    @Override
    public void run(String... args) throws Exception {

        //Crear rol secretario si no existe
        Rol secretarioRol;
        try {
            secretarioRol = rolServicio.buscarPorCargo(Cargo.SECRETARIO);
        } catch (Exception e) {
            secretarioRol = new Rol();
            secretarioRol.setCargo(Cargo.SECRETARIO);
            secretarioRol = rolServicio.guardarRol(secretarioRol);
            System.out.println("Secretario inicial creado por defecto");
        }

        //Crear usuario secretario si no existe
        String emailSecretario = "secretario@virtualacademy.com";
        try {
            usuarioServicio.buscarPorEmail(emailSecretario);
            System.out.println("Ya existe un secretario");
        } catch (Exception e) {
            Usuario secretario = new Usuario();
            secretario.setNombre("Secretario1");
            secretario.setApellidos("Prueba");
            secretario.setEmail(emailSecretario);
            secretario.setContraseña("Admin123");
            secretario.setDni("12345678A");
            secretario.setTelefono("600123456");
            secretario.setRol(secretarioRol);
            usuarioServicio.guardarUsuario(secretario);
            System.out.println("Secretario creado por defecto");
        }
    }
}
