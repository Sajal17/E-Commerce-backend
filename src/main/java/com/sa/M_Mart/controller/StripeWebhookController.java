package com.sa.M_Mart.controller;

import com.sa.M_Mart.model.Payment;
import com.sa.M_Mart.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/stripe/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping
    public String handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            com.stripe.model.Event event = com.stripe.net.Webhook.constructEvent(
                    payload, sigHeader, webhookSecret
            );

            if ("payment_intent.succeeded".equals(event.getType())) {
                com.stripe.model.PaymentIntent intent = (com.stripe.model.PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow();

                Payment payment = paymentRepository.findByTransactionId(intent.getId())
                        .orElseThrow(() -> new RuntimeException("Payment not found"));

                payment.setStatus(Payment.PaymentStatus.COMPLETED);
            } else if ("payment_intent.payment_failed".equals(event.getType())) {
                com.stripe.model.PaymentIntent intent = (com.stripe.model.PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow();

                Payment payment = paymentRepository.findByTransactionId(intent.getId())
                        .orElseThrow(() -> new RuntimeException("Payment not found"));

                payment.setStatus(Payment.PaymentStatus.FAILED);
            }

            return "OK";

        } catch (Exception e) {
            return "Webhook error: " + e.getMessage();
        }
    }
}
