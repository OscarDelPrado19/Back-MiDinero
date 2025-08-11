package com.midinero.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailRecuperacion(String email, String token) {
        try {
            // Leer plantilla desde resources/email.html
            ClassPathResource resource = new ClassPathResource("email.html");
            String html = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

            // Reemplazar marcador por el token
            html = html.replace("{{TOKEN}}", token);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name()
            );

            helper.setTo(email);
            helper.setSubject("Recuperación de Contraseña - MiDinero");
            helper.setText(html, true); // true = contenido HTML

            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
        }
    }
}
