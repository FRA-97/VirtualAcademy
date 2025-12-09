package com.fra.VirtualAcademy.controladores;

import com.fra.VirtualAcademy.modelos.Usuario;
import com.fra.VirtualAcademy.modelos.UsuarioAsignatura;
import com.fra.VirtualAcademy.servicios.UsuarioServicio;

import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.awt.*;

@Controller
@RequestMapping("/informacion")
public class EstudiantePdfControlador {

    //Clase encargada de generar un pdf con los datos del estudiante correspondiente

    private final UsuarioServicio usuarioServicio;

    public EstudiantePdfControlador(UsuarioServicio usuarioServicio) {
        this.usuarioServicio = usuarioServicio;
    }

    //Obtiene la información del estudiante activo
    @GetMapping("/estudiantes/{id}/pdf")
    public void exportarPdf(@PathVariable Long id,
                            HttpServletResponse response,
                            Authentication authentication) throws Exception {

        //Valida la identidad del usuario
        String emailUsuario = authentication.getName();
        Usuario usuarioActual = usuarioServicio.buscarPorEmail(emailUsuario);

        if (!usuarioActual.getIdUsuario().equals(id)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "No tienes permiso para acceder a este PDF");
            return;
        }

        Usuario estudiante = usuarioServicio.buscarPorId(id);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=Estudiante_" +
                        estudiante.getNombre() + "_" +
                        estudiante.getApellidos() + ".pdf");

        Document documento = new Document(PageSize.A4);
        PdfWriter.getInstance(documento, response.getOutputStream());
        documento.open();

        //Configuración del encabezado del pdf
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setWidths(new float[]{1, 6});

        try {
            Image logo = Image.getInstance("src/main/resources/static/imagenes/logo.jpg");
            logo.scaleToFit(60, 60);

            PdfPCell celdaLogo = new PdfPCell(logo);
            celdaLogo.setBorder(Rectangle.NO_BORDER);
            celdaLogo.setHorizontalAlignment(Element.ALIGN_LEFT);
            celdaLogo.setVerticalAlignment(Element.ALIGN_MIDDLE);
            celdaLogo.setPadding(5);
            header.addCell(celdaLogo);

        } catch (Exception e) {
            PdfPCell celdaLogo = new PdfPCell(new Phrase(""));
            celdaLogo.setBorder(Rectangle.NO_BORDER);
            header.addCell(celdaLogo);
        }

        PdfPCell tituloApp = new PdfPCell(
                new Phrase("Virtual Academy",
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22))
        );
        tituloApp.setBorder(Rectangle.NO_BORDER);
        tituloApp.setHorizontalAlignment(Element.ALIGN_LEFT);
        tituloApp.setVerticalAlignment(Element.ALIGN_MIDDLE);
        tituloApp.setPadding(10);

        header.addCell(tituloApp);
        documento.add(header);

        documento.add(new Paragraph("\n"));

        //Título principal del pdf
        Paragraph titulo = new Paragraph("INFORMACIÓN DEL ESTUDIANTE",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE));
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);

        documento.add(new Paragraph("\n"));

        //Tabla de datos del estudiante del pdf
        PdfPTable tablaDatos = new PdfPTable(2);
        tablaDatos.setWidthPercentage(100);
        tablaDatos.setSpacingBefore(10);
        tablaDatos.setWidths(new float[]{3, 7});

        Color azulFondo = new Color(30, 58, 138);
        Font fontTituloCelda = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        Font fontValorCelda = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);

        tablaDatos.addCell(getCeldaTitulo("Nombre", azulFondo, fontTituloCelda));
        tablaDatos.addCell(getCeldaValor(estudiante.getNombre(), fontValorCelda));
        tablaDatos.addCell(getCeldaTitulo("Apellidos", azulFondo, fontTituloCelda));
        tablaDatos.addCell(getCeldaValor(estudiante.getApellidos(), fontValorCelda));
        tablaDatos.addCell(getCeldaTitulo("Email", azulFondo, fontTituloCelda));
        tablaDatos.addCell(getCeldaValor(estudiante.getEmail(), fontValorCelda));
        tablaDatos.addCell(getCeldaTitulo("DNI", azulFondo, fontTituloCelda));
        tablaDatos.addCell(getCeldaValor(estudiante.getDni(), fontValorCelda));
        tablaDatos.addCell(getCeldaTitulo("Teléfono", azulFondo, fontTituloCelda));
        tablaDatos.addCell(getCeldaValor(estudiante.getTelefono(), fontValorCelda));

        documento.add(tablaDatos);
        documento.add(new Paragraph("\n"));

        //Título de las asignaturas del pdf
        Paragraph tituloAsignaturas = new Paragraph("ASIGNATURAS Y CALIFICACIONES",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLUE));
        tituloAsignaturas.setAlignment(Element.ALIGN_CENTER);

        documento.add(tituloAsignaturas);
        documento.add(new Paragraph("\n"));

        //Tabla de datos de la asignatura del pdf
        PdfPTable tablaAsignaturas = new PdfPTable(2);
        tablaAsignaturas.setWidthPercentage(100);
        tablaAsignaturas.setWidths(new float[]{7, 3});
        tablaAsignaturas.addCell(getCeldaTitulo("Asignatura", azulFondo, fontTituloCelda));
        tablaAsignaturas.addCell(getCeldaTitulo("Calificación", azulFondo, fontTituloCelda));

        for (UsuarioAsignatura ua : estudiante.getUsuarioAsignaturas()) {
            tablaAsignaturas.addCell(getCeldaValor(ua.getAsignatura().getNombre(), fontValorCelda));
            String nota = ua.getCalificacion() != null ?
                    ua.getCalificacion().toString() : "Sin calificar";
            tablaAsignaturas.addCell(getCeldaValor(nota, fontValorCelda));
        }
        documento.add(tablaAsignaturas);
        documento.close();
    }

    //Métodos auxiliares para configurar el pdf
    private PdfPCell getCeldaTitulo(String texto, Color colorFondo, Font font) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, font));
        celda.setBackgroundColor(colorFondo);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setPadding(8);
        return celda;
    }

    private PdfPCell getCeldaValor(String texto, Font font) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, font));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setPadding(6);
        return celda;
    }
}
