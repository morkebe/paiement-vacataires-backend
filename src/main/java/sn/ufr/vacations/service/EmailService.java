package sn.ufr.vacations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendCredentials(String toEmail, String username, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Vos identifiants de connexion - Gestion Vacations UFR");
            message.setText(buildCredentialsEmailBody(username, password));

            mailSender.send(message);
            log.info("Email d'identifiants envoyé à : {}", toEmail);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email à : {}", toEmail, e);
        }
    }

    private String buildCredentialsEmailBody(String username, String password) {
        return String.format("""
            Bonjour,
            
            Votre compte vacataire a été créé avec succès.
            
            Voici vos identifiants de connexion :
            - Nom d'utilisateur : %s
            - Mot de passe : %s
            
            Vous pouvez vous connecter à l'adresse suivante :
            %s
            
            IMPORTANT : Veuillez changer votre mot de passe lors de votre première connexion.
            
            Cordialement,
            L'équipe de Gestion des Vacations
            """, username, password, baseUrl);
    }
}