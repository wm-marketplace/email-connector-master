package com.wavemaker.connector.email;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.wavemaker.connector.exception.EmailTemplateNotFoundException;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EmailConnectorTestConfiguration.class)
public class EmailConnectorTest {

    @Autowired
    private EmailConnector emailConnector;

    @Test
    public void setEmailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.quitwait", "false");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "true");
        properties.setProperty("mail.debug", "true");
        properties.setProperty("mail.transport.protocol", "smtps");
        emailConnector.setEmailProperties(properties);
        Properties emailProperties = emailConnector.getEmailProperties();
        System.out.println(emailProperties.getProperty("mail.transport.protocol"));
        System.out.println(emailProperties.getProperty("mail.smtp.socketFactory.fallback"));
    }

    @Test
    public void sendMimeMail() {
        emailConnector.sendMimeMail(new MimeMessagePreparator() {
            @Override
            public void prepare(final MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.addTo("test@wavemaker.com");
                mimeMessageHelper.setFrom("wavemaker789@gmail.com");
                mimeMessageHelper.setSubject("testing mail service");
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                MimeMultipart mimeMultipart = new MimeMultipart();
                String htmlContent = "<html><h1>Hi</h1><p>Nice to meet you!</p></html>";
                mimeBodyPart.setContent(htmlContent, "text/html");
                mimeMultipart.addBodyPart(mimeBodyPart);
                mimeMessageHelper.getMimeMessage().setContent(mimeMultipart);
                System.out.println("Mail Sent!!");
            }
        });
    }

    @Test
    public void sendTemplateMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("WaveMaker Invites You");
        message.setTo("test@wavemaker.com");
        message.setFrom("wavemaker789@gmail.com");
        Map<String, String> props = new HashMap<>();
        props.put("user", "Mike");
        try {
            emailConnector.sendSimpleMailMessageWithTemplate(message, "templates/invitationtemplate", props);
        } catch (EmailTemplateNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendMailWithSimpleMailMessage() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("wavemaker789@gmail.com");
        simpleMailMessage.setTo("test@wavemaker.com");
        simpleMailMessage.setSubject("testing mail");
        simpleMailMessage.setText("hello world!!!");
        emailConnector.sendSimpleMailMessage(simpleMailMessage);
    }

    @Test
    public void sendMailWithMessagePreparator() {

        emailConnector.sendMimeMail(new MimeMessagePreparator() {
            @Override
            public void prepare(final MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.addTo("test@wavemaker.com");
                mimeMessageHelper.setFrom("wavemaker789@gmail.com");
                mimeMessageHelper.setText("hi hello mime message!!!");
                mimeMessageHelper.setSubject("testing mail service");
                mimeMessageHelper.addAttachment("myfile", new File("/home/sunilp/junk/two.xml"));
            }
        });
    }

    @Test
    public void sendInlineMail() {
        emailConnector.sendMimeMail(new MimeMessagePreparator() {
            @Override
            public void prepare(final MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.addTo("test@wavemaker.com");
                mimeMessageHelper.setFrom("wavemaker789@gmail.com");
                mimeMessageHelper.setSubject("testing mail service");

                MimeMultipart mimeMultipart = new MimeMultipart();


                MimeBodyPart htmlpart = new MimeBodyPart();
                String htmlMessage = "<html>Hi there,<br>";
                htmlMessage += "See this cool pic: <img src=\"cid:AbcXyz123\" />";
                htmlMessage += "</html>";
                htmlpart.setContent(htmlMessage, "text/html");


                File imageFilePath = new File("/home/sunilp/Downloads/IMG_20200202_115106.jpg");
                MimeBodyPart imagePart = new MimeBodyPart();
                imagePart.setHeader("Content-ID", "<AbcXyz123>");
                imagePart.setDisposition(MimeBodyPart.INLINE);
                imagePart.attachFile(imageFilePath);

                mimeMultipart.addBodyPart(htmlpart);
                mimeMultipart.addBodyPart(imagePart);
                mimeMessageHelper.getMimeMessage().setContent(mimeMultipart);
            }
        });
    }

}