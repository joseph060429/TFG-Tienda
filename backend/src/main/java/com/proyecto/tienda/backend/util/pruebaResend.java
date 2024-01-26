package com.proyecto.tienda.backend.util;

import java.util.List;
import java.util.Map;

import com.resend.Resend;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import com.resend.services.emails.model.Tag;

public class pruebaResend {
    public static void main(String[] args) {
        try {
            Resend resend = new Resend("re_393G2hLj_KzwvfiMXCwrGfw2Bum4Ew7LT");

            Attachment att = Attachment.builder()
                    .fileName("invoice.pdf")
                    .content("invoiceBuffer")
                    .build();

            Tag tag = Tag.builder()
                    .name("category")
                    .value("confirm_email")
                    .build();

            SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                    .from("administracion@jastoredcomponents.es")
                    .to("sepulmonte29@gmail.com")
                    .attachments(List.of(att))
                    .text("hello world")
                    .subject("it works!")
                    .headers(Map.of(
                            "X-Entity-Ref-ID", "123456789"
                    ))
                    .tags(tag)
                    .build();

            SendEmailResponse data = resend.emails().send(sendEmailRequest);
        } catch (Exception e) {
            // Manejar la excepción aquí
            e.printStackTrace();
        }
    }
}

