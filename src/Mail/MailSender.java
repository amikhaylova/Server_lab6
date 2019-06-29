package Mail;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {

    final String email_from = "shorty.manager@gmail.com";
    final String password = "whatfor123&";
    final String host = "smtp.gmail.com";
    final String port = "587";

    public void send(String subject_line, String message, String email_to) throws MessagingException{
        System.out.println("Идет отправка сообщения.");

        java.util.Properties props = new java.util.Properties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.connectiontimeout", "5000");


        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email_from, password);
            }
        });



// Конструируем сообщение

        Message msg = new MimeMessage(session);

        try {

            msg.setFrom(new InternetAddress(email_from));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email_to));
            msg.setSubject(subject_line);
            msg.setText(message);

// Отправляем сообщение
            Transport.send(msg);
            System.out.println("Сообщение отправлено!");

        }catch (AuthenticationFailedException e){
            System.out.println("Возникла ошибка: " + e.getMessage());
        }
    }
}
