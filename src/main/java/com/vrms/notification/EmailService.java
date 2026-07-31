package com.vrms.notification;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    private final String username;
    private final String password;

    public EmailService(String username,String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Email username is required");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email password is required");
        }

        this.username = username.trim();
        this.password = password.trim();
    }

    public static EmailService fromEnvironment() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String username = dotenv.get("EMAIL_USERNAME");
        String password = dotenv.get("EMAIL_PASSWORD");

        if (username == null || username.trim().isEmpty()) {
            username = System.getenv("EMAIL_USERNAME");
        }

        if (password == null || password.trim().isEmpty()) {
            password = System.getenv("EMAIL_PASSWORD");
        }

        return new EmailService(username,password);
    }
    private static final String DEFAULT_TIMEOUT_MS = "10000";
    private static final String DEFAULT_ENCODING = "UTF-8";
    public void sendEmail(String recipient,String subject,String body) {
        if (recipient == null || recipient.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient email is required");
        }

        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Email subject is required");
        }

        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Email body is required");
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.starttls.required","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");
        properties.put("mail.smtp.ssl.trust","smtp.gmail.com");
        properties.put("mail.smtp.connectiontimeout", DEFAULT_TIMEOUT_MS);
        properties.put("mail.smtp.timeout", DEFAULT_TIMEOUT_MS);
        properties.put("mail.smtp.writetimeout", DEFAULT_TIMEOUT_MS);

        Session session = Session.getInstance(properties,new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username,password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(username, "VRMS Vehicle Rental System", DEFAULT_ENCODING));
            message.setReplyTo(new Address[] { new InternetAddress(username) });
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient.trim()));
            message.setSubject(subject.trim(), DEFAULT_ENCODING);
            message.setText(body, DEFAULT_ENCODING);
            message.setSentDate(new Date());

            Transport.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send email",e);
        }
    }
}