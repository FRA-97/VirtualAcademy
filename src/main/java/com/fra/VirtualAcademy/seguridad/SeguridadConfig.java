package com.fra.VirtualAcademy.seguridad;

import com.fra.VirtualAcademy.seguridad.filter.JwtAuthenticationFilter;
import com.fra.VirtualAcademy.seguridad.filter.JwtValidationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    public SeguridadConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    //AuthenticationManager: Spring lo usa para autenticar usuarios mediante UserDetailsService
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //PasswordEncoder: encripta contraseñas con BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Configuración principal de seguridad
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authManager = authenticationManager();

        //Filtro que maneja el login y genera JWT
        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(authManager);

        //Filtro que valida JWT en cada petición posterior
        JwtValidationFilter jwtValidationFilter =
                new JwtValidationFilter(authManager);

        http
                //Desactiva CSRF para APIs y JWT
                .csrf(csrf -> csrf.disable())

                //Permite que Spring guarde automáticamente el SecurityContext si hace falta
                .securityContext(context -> context.requireExplicitSave(false))

                //Manejo de sesiones
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                //Autorización de rutas
                .authorizeHttpRequests(auth -> auth
                        //Recursos estáticos públicos
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/imagenes/**", "/webjars/**").permitAll()

                        //Pantalla de login
                        .requestMatchers("/login-page").permitAll()

                        //Endpoint de autenticación JWT
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()


                        //REGLAS POR ROLES
                        //ESTUDIANTE: solo puede entrar a informacion/estudiantes
                        .requestMatchers(
                                "/informacion/estudiantes",
                                "/informacion/estudiantes/**",
                                "/informacion/estudiantes/*/pdf"
                        ).hasRole("ESTUDIANTE")

                        //PROFESOR: solo a usuarioAsignatura/listado y formulario
                        //SECRETARIO también puede acceder aquí
                        .requestMatchers(
                                "/usuario-asignatura",
                                "/usuario-asignatura/**"
                        ).hasAnyRole("PROFESOR", "SECRETARIO")

                        .requestMatchers(
                                "/ayuda",
                                "/ayuda/**"
                        ).hasAnyRole("ESTUDIANTE", "PROFESOR", "SECRETARIO")

                        .requestMatchers("/acerca", "/acerca/**")
                        .hasAnyRole("ESTUDIANTE", "PROFESOR", "SECRETARIO")

                        //SECRETARIO: el resto de vistas
                        .anyRequest().hasRole("SECRETARIO")
                )

                //Filtros JWT en el orden correcto
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                //Después del filtro estándar de login, ponemos el validador de tokens
                .addFilterAfter(jwtValidationFilter, UsernamePasswordAuthenticationFilter.class)

                //Si no está autenticado, redirigimos a la pantalla de login
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/login-page")
                        )
                )
                //Configuración de logout
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login-page")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
        );

        return http.build();
    }
}
