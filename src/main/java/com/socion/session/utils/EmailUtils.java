package com.socion.session.utils;

import com.socion.session.config.AppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.Properties;


public class EmailUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtils.class);

    private EmailUtils() {
    }


    public static void sendEmail(AppContext appContext, String emailId, Long totalrecords, Long successrecords, Long failedrecords, String timeZones) throws IOException, MessagingException {
        LOGGER.debug("Sending email by setting required smtp properties.");
        Properties props = new Properties();
        props.put(appContext.getSmtpAuth(), Constants.TRUE);
        props.put(appContext.getSmtpMailTls(), Constants.TRUE);
        props.put(appContext.getSmtpHost(), appContext.getSmtpMail());
        props.put(appContext.getSmtpPort(), appContext.getPort());
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(appContext.getSourceEmail(), appContext.getSourceEmailPassword());
            }
        });
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(appContext.getSourceEmail(), false));
        msg.setSubject(appContext.getCronEmailSubject());
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailId));


        LOGGER.debug("Sending email to the user {}", "");


        String emailContent = new String(Files.readAllBytes(Paths.get(appContext.getCronEmailPath())));

        emailContent = emailContent.replace("$timezones", timeZones);
        emailContent = emailContent.replace("$date", LocalDate.now().toString());
        emailContent = emailContent.replace("$totalrecords", totalrecords.toString());
        emailContent = emailContent.replace("$successrecords", successrecords.toString());
        emailContent = emailContent.replace("$failedrecords", failedrecords.toString());
        msg.setContent(emailContent, "text/html");
        msg.setSentDate(new Date());
        Transport.send(msg);
    }

}

