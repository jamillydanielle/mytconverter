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

   @PostMapping("/sendPasswordResetEmail")
    public ResponseEntity<String> sendPasswordResetEmail(
            @RequestParam String email,
            @RequestParam String resetLink) {
        
        String subject = "Recuperação de Senha - MyTConvert";
        String message = "Olá,\n\n" +
                "Recebemos uma solicitação para redefinir sua senha no MyTConvert.\n\n" +
                "Para redefinir sua senha, clique no link abaixo ou copie e cole no seu navegador:\n\n" +
                resetLink + "\n\n" +
                "Este link é válido por 30 minutos.\n\n" +
                "Se você não solicitou a redefinição de senha, ignore este email.\n\n" +
                "Atenciosamente,\n" +
                "Equipe MyTConvert";
        
        emailService.sendEmail(email, subject, message);
        
        return ResponseEntity.ok("Email de reset enviado com sucesso");
    }
}
