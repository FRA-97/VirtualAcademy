package com.fra.VirtualAcademy.seguridad;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

//Mixin para poder deserializar SimpleGrantedAuthority desde JSON
public abstract class SimpleGrantedAuthorityJsonCreator {

    @JsonCreator
    public SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role) {
        //No necesita implementación pues funciona como puente para el JSON
    }
}