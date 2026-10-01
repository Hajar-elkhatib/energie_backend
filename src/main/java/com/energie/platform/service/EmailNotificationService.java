package com.energie.platform.service;

import com.energie.platform.model.RendezVous;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/** Sends the e-mails related to an appointment after an administrator action. */
@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.mail", name = "enabled", havingValue = "true")
public class EmailNotificationService {
  private static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("EEEE d MMMM uuuu", Locale.FRENCH);

  private final JavaMailSender mailSender;

  @Value("${app.mail.from:}")
  private String from;

  public boolean envoyerConfirmationRendezVous(RendezVous rendezVous) {
    return envoyer(
        rendezVous,
        "EcoReno+ - Votre rendez-vous est confirmé",
        "Votre rendez-vous a été confirmé par notre équipe.");
  }

  public boolean envoyerAnnulationRendezVous(RendezVous rendezVous) {
    return envoyer(
        rendezVous,
        "EcoReno+ - Votre rendez-vous est annulé",
        "Votre rendez-vous a été annulé. Le créneau est de nouveau disponible.");
  }

  private boolean envoyer(RendezVous rendezVous, String objet, String introduction) {
    String emailVisiteur = rendezVous.getVisiteur().getEmail();
    try {
      if (from.isBlank()) {
        log.error("E-mail relatif au rendez-vous {} non envoyé : MAIL_FROM n'est pas configuré", rendezVous.getId());
        return false;
      }
      SimpleMailMessage message = new SimpleMailMessage();
      message.setFrom(from);
      message.setTo(emailVisiteur);
      message.setSubject(objet);
      message.setText("Bonjour,\n\n"
          + introduction + "\n\n"
          + "Date : " + DATE_FORMAT.format(rendezVous.getDate()) + "\n"
          + "Heure : " + rendezVous.getHeure() + "\n\n"
          + "À bientôt,\nL'équipe EcoReno+");
      mailSender.send(message);
      log.info("E-mail relatif au rendez-vous {} envoyé à {}", rendezVous.getId(), emailVisiteur);
      return true;
    } catch (MailException | IllegalArgumentException exception) {
      log.error("E-mail relatif au rendez-vous {} non envoyé vers {}", rendezVous.getId(), emailVisiteur, exception);
      return false;
    }
  }
}
