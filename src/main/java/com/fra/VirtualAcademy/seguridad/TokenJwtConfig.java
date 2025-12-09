package com.fra.VirtualAcademy.seguridad;

import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;

public class TokenJwtConfig {

    //Llave secreta para firmar y verificar los JWT
    public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

    //Prefijo estándar para tokens en cabecera Authorization
    public static final String PREFIX_TOKEN = "Bearer ";

    //Nombre de la cabecera HTTP donde se envía el token
    public static final String HEADER_AUTHORIZATION = "Authorization";

    //Tipo de contenido usado para las respuestas JSON en los filtros
    public static final String CONTENT_TYPE = "application/json";
}