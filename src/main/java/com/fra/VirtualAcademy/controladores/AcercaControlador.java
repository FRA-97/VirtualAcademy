package com.fra.VirtualAcademy.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AcercaControlador {

    //Clase encargada de mostrar la pantalla de Acerca de

    @GetMapping("/acerca")
    public String mostrarAcerca() {
        return "acerca/acerca";
    }
}
