package com.fra.VirtualAcademy.seguridad.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fra.VirtualAcademy.seguridad.SimpleGrantedAuthorityJsonCreator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.*;

import static com.fra.VirtualAcademy.seguridad.TokenJwtConfig.*;

public class JwtValidationFilter extends OncePerRequestFilter {

    @SuppressWarnings("unused")
    private final AuthenticationManager authenticationManager;

    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        //Se obtiene la cabecera Authorization del request
        String header = request.getHeader(HEADER_AUTHORIZATION);

        //Si no hay token o no empieza con "Bearer ", se deja pasar la request sin validar JWT
        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            chain.doFilter(request, response);
            return;
        }

        //Se elimina el prefijo "Bearer" para obtener solo el token
        String token = header.replace(PREFIX_TOKEN, "");

        try {
            //Validación del token utilizando la SECRET_KEY configurada
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            //Extraemos el nombre de usuario
            String username = claims.get("username", String.class);
            //Extraemos el rol
            Object authoritiesClaims = claims.get("authorities");

            //Reconstrucción del listado de roles a partir del JSON
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.asList(new ObjectMapper()
                            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                            .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));

            //Creamos un token de autenticación con el username y sus roles
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            //Guardamos la autenticación en el contexto de Spring Security
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            //Continuamos la ejecución del resto de filtros
            chain.doFilter(request, response);

        } catch (JwtException e) {
            //Si el token es inválido o expiró, devolvemos un error personalizado
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT es inválido");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(CONTENT_TYPE);
        }
    }
}