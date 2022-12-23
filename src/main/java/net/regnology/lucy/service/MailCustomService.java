package net.regnology.lucy.service;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;

/**
 * Custom service for sending emails.
 */
@Service
public class MailCustomService extends MailService {

    public MailCustomService(
        JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine
    ) {
        super(jHipsterProperties, javaMailSender, messageSource, templateEngine);
    }
}
