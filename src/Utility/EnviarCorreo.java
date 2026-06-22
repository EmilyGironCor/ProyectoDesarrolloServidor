/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

import java.io.File;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author emily
 */
public class EnviarCorreo {
     private static final String CORREO_EMISOR = "desarrolloiiiproyecto@gmail.com";
    private static final String CLAVE_APP = "wqnl nolr hjeq nrsd"; // contraseña de aplicación de Gmail

    public static void enviarPDF(String destinatario, String asunto, String mensaje, String rutaPDF) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(CORREO_EMISOR, CLAVE_APP);
            }
        });

        Message mail = new MimeMessage(session);
        mail.setFrom(new InternetAddress(CORREO_EMISOR));
        mail.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        mail.setSubject(asunto);

        // Cuerpo del mensaje
        BodyPart cuerpoTexto = new MimeBodyPart();
        cuerpoTexto.setText(mensaje);

        // Adjunto PDF
        BodyPart cuerpoAdjunto = new MimeBodyPart();
        cuerpoAdjunto.setDataHandler(new DataHandler(
                new FileDataSource(new File(rutaPDF))
        ));
        cuerpoAdjunto.setFileName(new File(rutaPDF).getName());

        Multipart multiparte = new MimeMultipart();
        multiparte.addBodyPart(cuerpoTexto);
        multiparte.addBodyPart(cuerpoAdjunto);

        mail.setContent(multiparte);

        Transport.send(mail);
        System.out.println("Correo enviado a: " + destinatario);
    }
}
