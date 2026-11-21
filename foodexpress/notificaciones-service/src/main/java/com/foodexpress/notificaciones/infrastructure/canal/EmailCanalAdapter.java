package com.foodexpress.notificaciones.infrastructure.canal;

import com.foodexpress.notificaciones.application.port.out.CanalNotificacion;
import com.foodexpress.notificaciones.domain.exception.EnvioFallidoException;
import com.foodexpress.notificaciones.domain.model.Canal;
import com.foodexpress.notificaciones.domain.model.Mensaje;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * PATRÓN ADAPTER: adapta JavaMailSender (SMTP) a la interfaz CanalNotificacion.
 *
 * Cambiar de proveedor de correo (SendGrid, Mailgun) es escribir otro adaptador
 * de EMAIL; los casos de uso no se enteran.
 *
 * Si no hay servidor de correo configurado (spring.mail.host), el envío se simula
 * escribiendo el mensaje en el log, para poder ejecutar el servicio sin SMTP.
 */
@Component
public class EmailCanalAdapter implements CanalNotificacion {

    private static final Logger log = LoggerFactory.getLogger(EmailCanalAdapter.class);

    private final JavaMailSender mailSender;
    private final String remitente;

    public EmailCanalAdapter(ObjectProvider<JavaMailSender> mailSender,
                             @Value("${foodexpress.notificaciones.remitente}") String remitente) {
        this.mailSender = mailSender.getIfAvailable();
        this.remitente = remitente;
    }

    @Override
    public Canal canal() {
        return Canal.EMAIL;
    }

    @Override
    public void enviar(Mensaje mensaje) {
        if (!mensaje.destinatario().tieneEmail()) {
            throw new EnvioFallidoException("El destinatario no tiene correo electrónico");
        }
        String para = mensaje.destinatario().email();

        if (mailSender == null) {
            log.info("[EMAIL simulado] para={} asunto='{}' cuerpo='{}'", para, mensaje.asunto(), mensaje.cuerpo());
            return;
        }

        SimpleMailMessage correo = new SimpleMailMessage();
        correo.setFrom(remitente);
        correo.setTo(para);
        correo.setSubject(mensaje.asunto());
        correo.setText(mensaje.cuerpo());
        try {
            mailSender.send(correo);
        } catch (MailException e) {
            throw new EnvioFallidoException("No se pudo enviar el correo a " + para, e);
        }
    }
}
