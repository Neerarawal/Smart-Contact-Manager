package com.smart.service;

import java.util.Properties;
import jakarta.mail.Message;  // ✅ Correct Jakarta Mail import
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;
@Service
public class EmailService {

	//code 
	
	public boolean sendEmail(String subject, String message, String to) {
        boolean f = false;

        String from = "rawalkrishna544@gmail.com";
        String host = "smtp.gmail.com";

        // Set Mail Server Properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587"); // Use 587 for TLS
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Use TLS for security

        // Session with Authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "eyhj wxeb xcki jqnx"); // Use App Password
            }
        });

        session.setDebug(true); // Enable debug mode

        try {
            // Creating a Message
            MimeMessage m = new MimeMessage(session);
            m.setFrom(new InternetAddress(from));
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setSubject(subject);
          //  m.setText(message);
            m.setContent(message,"text/html");

            // Send Email
            Transport.send(m);
            System.out.println("📧 Email sent successfully!");
            f = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }
	
	
}
