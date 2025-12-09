package com.fra.VirtualAcademy.seguridad.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fra.VirtualAcademy.dto.LoginRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static com.fra.VirtualAcademy.seguridad.TokenJwtConfig.*;
import io.jsonwebtoken.Jwts;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        //Este filtro, por defecto, intercepta las peticiones POST a /login.
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {

        try {
            //Leemos el JSON enviado en el body del login y lo convertimos a LoginRequestDto
            LoginRequestDto loginRequest =
                    new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            String email = loginRequest.email();
            String password = loginRequest.password();

            //Spring Security usa este token para intentar autenticar al usuario
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManager.authenticate(authenticationToken);

        } catch (IOException e) {
            throw new RuntimeException("Error leyendo credenciales de login", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {

        //Guardamos la autenticación en el contexto de Spring Security
        SecurityContextHolder.getContext().setAuthentication(authResult);
        //Persistimos la autenticación en la sesión HTTP
        request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        //Obtenemos el usuario autenticado y sus roles
        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) authResult.getPrincipal();

        String username = user.getUsername();
        Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();

        //Convertimos roles a JSON para guardarlos como claim en el token
        String rolesJson = new ObjectMapper().writeValueAsString(roles);

        //Construcción del token JWT
        String token = Jwts.builder()
                .subject(username)
                .claim("username", username)
                .claim("authorities", rolesJson)
                .issuedAt(new Date())
                //Expira en una hora
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(SECRET_KEY)
                .compact();

        //Cuerpo de la respuesta personalizada
        Map<String, String> body = new HashMap<>();
        body.put("token", PREFIX_TOKEN + token);
        body.put("username", username);

        //Se envía el rol
        String role = user.getAuthorities().iterator().next().getAuthority();
        body.put("role", role);

        //Añadimos el token también en la cabecera Authorization
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

        //Se especifica UTF-8 para evitar problemas con acentos o caracteres especiales
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException {

        //Respuesta personalizada cuando la autenticación falla
        Map<String, String> body = new HashMap<>();
        body.put("message", "Usuario o contraseña incorrectos");
        body.put("error", failed.getMessage());

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}