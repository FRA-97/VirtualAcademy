package com.fra.VirtualAcademy.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AyudaControlador {

    //Clase encargada de mostrar la pantalla de Ayuda

    @GetMapping("/ayuda")
    public String mostrarAyuda() {
        return "ayuda/ayuda";
    }
}

