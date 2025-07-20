package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailSender
{
    public static String getPassword()
    {
        String appPassowed = null;
        try
        {
            appPassowed = new String(Files.readAllBytes(Paths.get("app.pass")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return appPassowed;

    }

    public static String getHtmlContent(String templateName)
    {
        String htmlTemplate = null;
        try
        {
            htmlTemplate = new String(Files.readAllBytes(Paths.get("email-templates" + File.separator + templateName)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return htmlTemplate;
    }

    public static void smtpSendEmail(String templateName) throws MessagingException
    {
        String host = "smtp.gmail.com";
        String port = "587";
        String username = "abelbenardm@gmail.com";
        String password = getPassword();

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(username, password);
            }
        });
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("william.ochomo@skyworld.co.ke"));

        String subject = templateName.replace(".html", "").replace("-template", "").replace("-", " ");
        message.setSubject(subject);

        String htmlConent = getHtmlContent(templateName);
        message.setContent(htmlConent, "text/html; charset=utf-8");

        Transport.send(message);
        System.out.println(subject + "Email sent successfully.");

    }
    public static void main(String[] args)
    {
        List<String> templates = new ArrayList<>();
        templates.add("product-update-newsletter.html");
        templates.add("trial-expiration-template.html");
        templates.add("welcome-template.html");

        templates.stream()
        .forEach(template -> 
        {
            try
            {
                smtpSendEmail(template);
            }
            catch (MessagingException e)
            {
                e.printStackTrace();
            }
        });
        
    }
}
