package com.fra.VirtualAcademy.seguridad;

import com.fra.VirtualAcademy.modelos.Usuario;
import com.fra.VirtualAcademy.repositorios.UsuarioRepositorio;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;

    public UserDetailsServiceImpl(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        //Buscamos al usuario en la base de datos por su email
        //Si no se encuentra, se lanza una excepción para detener la autenticación
        Usuario usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("No existe usuario con email: " + email));

        //Crear autoridad (rol) a partir del campo usuario.getRol()
        GrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getCargo().name());

        //En este caso, solo existe un rol por usuario
        List<GrantedAuthority> authorities = List.of(authority);

        //Construcción del UserDetails que Spring Security utilizará internamente
        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail())
                .password(usuario.getContraseña())
                .authorities(authorities)
                //Configuración del estado de la cuenta
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}