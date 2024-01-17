package pl.polak.nikodem.whiteboard.services.interfaces;

public interface MailSenderService {
    void sendResetPasswordMessage(String to, String subject, String text);
    String prepareResetPasswordMailText(String token);
}
