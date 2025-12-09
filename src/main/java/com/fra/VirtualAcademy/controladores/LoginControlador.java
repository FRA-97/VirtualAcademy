package com.fra.VirtualAcademy.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginControlador {

    //Clase encargada de mostrar el inisio de sesión

    @GetMapping("/login-page")
    public String loginPage() {
        return "login";
    }
}
