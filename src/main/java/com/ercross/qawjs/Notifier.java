package com.ercross.qawjs;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Notifier {
    private Notifier () {
        throw  new IllegalStateException();
    }

    public static void notifyByEmail(String emailRecipient, String subject, String messageBody) {
        String sender = "tobins4u@gmail.com";
        String recipient = emailRecipient;
        String host = "localhost";

        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", host);

        Session session = Session.getDefaultInstance(props);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setText(messageBody);
            Transport.send(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}