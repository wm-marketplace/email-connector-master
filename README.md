## Connector  Introduction

Connector is a Java based backend extension for WaveMaker applications. Connectors are built as Java modules & exposes java based SDK to interact with the connector implementation.
Each connector is built for a specific purpose and can be integrated with one of the external services. Connectors are imported & used in the WaveMaker application. Each connector runs on its own container thereby providing the ability to have it’s own version of the third party dependencies.

## Features of Connectors

1. Connector is a java based extension which can be integrated with external services and reused in many Wavemaker applications.
1. Each connector can work as an SDK for an external system.
1. Connectors can be imported once in a WaveMaker application and used many times in the applications by creating multiple instances.
1. Connectors are executed in its own container in the WaveMaker application, as a result there are no dependency version conflict issues between connectors.

## About Email Connector

## Email Connector Introduction
This connector will provides an easy apis to send bulk emails from WaveMaker application.

## Prerequisite

1. Email server with server host name & port no and email server credentials
1. Java 1.8 or above
1. Maven 3.1.0 or above
1. Any java editor such as Eclipse, Intelij..etc
1. Internet connection


## Build
You can build this connector using following command
```
mvn clean install
```

## Deploy
You can import connector dist/email.zip artifact in WaveMaker studio application using file upload option.
On after deploying email-connector in the WaveMaker studio application, make sure to update connector properties in the profile properties.Such as email server host name and port no, email server credentials


# Use Email Connector in WaveMaker

## Introduction
The **Email Connector** provides simplified APIs to integrate with any email service provider. It supports sending:

1. Plain text messages
2. Parameterized/templatized messages
3. Messages with attachments

## Steps to Send an Email

### 1. Importing the Email Connector into Your Project
- Download the latest **email-connector** zip [here](https://github.com/wm-marketplace/email-connector-master/releases).
- Import the downloaded zip into your app using the **Import Resource** option under the **Connector** folder.
![image](https://github.com/user-attachments/assets/134375f6-2400-41d1-b737-6a58aee4c1eb)
![image](https://github.com/user-attachments/assets/9f51e23c-c17e-4e14-9fc9-58dbf6dc4744)

### 2. Configure Email Connector Properties in Profiles
By default, externalized connector properties are added to project profiles. The properties include:

```properties
connector.email.default.email.server.host=
connector.email.default.email.server.password=
connector.email.default.email.server.port=
connector.email.default.email.server.username=
connector.email.default.email.transport.protocol=smtp
connector.email.default.email.server.sslenabled=true
```

Set appropriate values for these properties in profiles. These can also be accessed in Java services as follows:

#### Import Statement:
```java
import org.springframework.beans.factory.annotation.Value;

@Value("${email.server.username}")
private String fromEmailAddress;
```

### 3. Creating a Java Service
- Create a Java Service named **EmailService**.
- Import the **Email Connector API** class:

```java
import org.springframework.mail.SimpleMailMessage;
import com.wavemaker.connector.email.EmailConnector;
```

- Autowire the Email Connector API:

```java
@Autowired
private EmailConnector emailConnector;
```

## Sending Emails

### 1. Send an Email with Plain Text
```java
@ExposeToClient
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private EmailConnector emailConnector;

    @Value("${email.server.username}")
    private String fromEmailAddress;

    public void sendMailWithSimpleMessage(String toEmailAddress, String emailSubject, String emailMessage) {
        logger.info("Sending the email to {} with subject {} and message {}", toEmailAddress, emailSubject, emailMessage);

        String[] recipientList = toEmailAddress.split(",");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromEmailAddress);
        simpleMailMessage.setTo(recipientList);
        simpleMailMessage.setSubject(emailSubject);
        simpleMailMessage.setText(emailMessage);
        emailConnector.sendSimpleMailMessage(simpleMailMessage);
    }
}
```

### 2. Send an Email with a Templatized Message
#### Required Imports:
```java
import java.util.HashMap;
import java.util.Map;
import com.wavemaker.connector.exception.EmailTemplateNotFoundException;
```

#### Sending an Email Using a Template:
```java
public void sendEmailWithTemplate(String toEmailAddress, String emailSubject) {
        logger.info("Sending the email to {} with subject {} ", toEmailAddress, emailSubject);
        String[] recipientList = toEmailAddress.split(",");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(emailSubject);
        message.setTo(recipientList);
        message.setFrom(fromEmailAddress);
        Map<String, String> props = new HashMap<>();
        props.put("user", "Mike");
        try {
            emailConnector.sendSimpleMailMessageWithTemplate(message, "templates/invitationtemplate.txt", props);
        } catch (EmailTemplateNotFoundException e) {
            throw new RuntimeException("Email template not found", e);
        }
}
```

**Note:** The template **invitationtemplate.txt** should be placed inside `src/main/resources/templates`.
![image](https://github.com/user-attachments/assets/b7cde1c5-03de-4272-b998-3c242435de7a)
![image](https://github.com/user-attachments/assets/89375884-fc92-420b-8983-58d20d9d30e0)

### 3. Send an Email with Attachments
#### Required Imports:
```java
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
```

#### Sending an Email with an Attachment:
```java
public void sendMailWithAttachment(String toEmailAddress, String emailSubject, String emailMessage) {
    logger.info("Sending email to {}, with subject : {}, message : {} and mimetype content", toEmailAddress, emailSubject, emailMessage);
    emailConnector.sendMimeMail(new MimeMessagePreparator() {
        @Override
        public void prepare(final MimeMessage mimeMessage) throws Exception {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.addTo(toEmailAddress);
            mimeMessageHelper.setFrom(fromEmailAddress);
            mimeMessageHelper.setSubject(emailSubject);
            mimeMessageHelper.setText(emailMessage);
            mimeMessageHelper.addAttachment("myfile", new ClassPathResource("files/RNDark.png"));
        }
    });
}
```
### 4. Send an Email with Mime Type
```java
public void sendMimeMail(String toEmailAddress, String emailSubject) {
        logger.info("Sending email to {}, with subject {} and mimetype content", toEmailAddress, emailSubject);

        emailConnector.sendMimeMail(new MimeMessagePreparator() {
            @Override
            public void prepare(final MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.addTo(toEmailAddress);
                mimeMessageHelper.setFrom(fromEmailAddress);
                mimeMessageHelper.setSubject(emailSubject);

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                MimeMultipart mimeMultipart = new MimeMultipart();
                String htmlContent = "<html><h1>Hi</h1><p>Nice to meet you!</p></html>";
                mimeBodyPart.setContent(htmlContent, "text/html");
                mimeMultipart.addBodyPart(mimeBodyPart);

                mimeMessageHelper.getMimeMessage().setContent(mimeMultipart);
            }
        });
}
```

### 4. Send Email with Attachment and Mime Message
```java
public void sendMimeMailWithAttachment(String toEmailAddress, String emailSubject) {
        emailConnector.sendMimeMail(new MimeMessagePreparator() {
            @Override
            public void prepare(final MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                mimeMessageHelper.addTo(toEmailAddress);
                mimeMessageHelper.setFrom(fromEmailAddress);
                mimeMessageHelper.setSubject(emailSubject);

                MimeMultipart mimeMultipart = new MimeMultipart();


                MimeBodyPart htmlpart = new MimeBodyPart();
                String htmlMessage = "<html>Hi there, ";
                htmlMessage += "See this cool pic: <img src=\"cid:AbcXyz123\" />";
                htmlMessage += "</html>";
                htmlpart.setContent(htmlMessage, "text/html");


                MimeBodyPart imagePart = new MimeBodyPart();
                imagePart.setHeader("Content-ID", "<AbcXyz123>");
                imagePart.setDisposition(MimeBodyPart.INLINE);
                imagePart.attachFile(new ClassPathResource("files/RNDark.png").getFile());

                mimeMultipart.addBodyPart(htmlpart);
                mimeMultipart.addBodyPart(imagePart);
                mimeMessageHelper.getMimeMessage().setContent(mimeMultipart);
            }
        });
}
```

## Integrating with the UI
- Create a Java Service Variable for **EmailService**.
- Create a Java Service Variable for the Java service created in the previous steps.
  ![image](https://github.com/user-attachments/assets/6fa9bec0-7413-489a-8f0c-03bbb6629f08)
- Use this service variable in your UI logic as required.

## Customizing Email Properties
To customize email properties, add the following to your Java Service:

#### Import Statement:
```java
import jakarta.annotation.PostConstruct;
```
Add the below code snippet to your Java Service. Here, we are setting the mail.smtp.starttls.enable property of emailconnector to false.
#### Custom Email Property Setup:
```java
@PostConstruct
public void settingEmailProperties() {
    Properties properties = new Properties();
    properties.setProperty("mail.smtp.starttls.enable", "false");
    emailConnector.setEmailProperties(properties);
}
```

---










