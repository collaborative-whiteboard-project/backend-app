package pl.polak.nikodem.whiteboard.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.polak.nikodem.whiteboard.services.interfaces.MailSenderService;

@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender mailSender;

    @Override
    public void sendResetPasswordMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("collaborative.whiteboard.app@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    @Override
    public String prepareResetPasswordMailText(String token) {
        return String.format("""
                To reset your password use this link:
                http://localhost:4200/reset/password/%s
                """, token);
    }
}
