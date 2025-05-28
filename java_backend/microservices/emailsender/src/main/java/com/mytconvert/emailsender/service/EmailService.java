package com.mytconvert.emailsender.service;

import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

@Getter
@Setter
@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    // Secret key for encryption
    @Value("${secret_key}")
    private String secretKeyString;
    
    private final ObjectMapper objectMapper = new ObjectMapper();


    public void sendEmail(String receiver, String subject, String text) {
        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setTo(receiver);
        mensagem.setSubject(subject);
        mensagem.setText(text);

        try {
            emailSender.send(mensagem);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error sending email.");
        }
    }
}