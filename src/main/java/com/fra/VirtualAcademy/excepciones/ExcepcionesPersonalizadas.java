package com.fra.VirtualAcademy.excepciones;

//Contiene todas las excepciones personalizadas para nuestra aplicación
public class ExcepcionesPersonalizadas {

    //Se lanza cuando se intenta crear un recurso que ya existe
    public static class RecursoDuplicado extends RuntimeException {
        public RecursoDuplicado(String mensaje) {
            super(mensaje);
        }
    }

    //Se lanza cuando un recurso solicitado no se encuentra en la base de datos
    public static class RecursoNoEncontrado extends RuntimeException {
        public RecursoNoEncontrado(String mensaje) {
            super(mensaje);
        }
    }

    //Se lanza cuando se envían datos incompletos o incorrectos
    public static class DatosInvalidos extends RuntimeException {
        public DatosInvalidos(String mensaje) {
            super(mensaje);
        }
    }

    //Se lanza cuando se intenta ejecutar una acción no permitida por las reglas del sistema
    public static class OperacionNoPermitida extends RuntimeException {
        public OperacionNoPermitida(String mensaje) {
            super(mensaje);
        }
    }
}
