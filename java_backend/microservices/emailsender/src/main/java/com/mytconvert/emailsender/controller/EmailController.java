package com.mytconvert.emailsender.controller;

import com.mytconvert.emailsender.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestParam String receiverEmail, @RequestParam String result) {
        if (receiverEmail == null || receiverEmail.isEmpty()) {
            return ResponseEntity.badRequest().body("You need to provide the receiver's email.");
        }

        String subject = "Test ";
        String text = "Envio de email teste";

        emailService.sendEmail(receiverEmail, subject, text);

        return ResponseEntity.ok("Email sent successfully!");
    }

}
