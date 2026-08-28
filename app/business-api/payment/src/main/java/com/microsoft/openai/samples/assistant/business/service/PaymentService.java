package com.microsoft.openai.samples.assistant.business.service;

import com.microsoft.openai.samples.assistant.business.models.Payment;
import com.microsoft.openai.samples.assistant.business.models.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

    private final WebClient.Builder webClientBuilder;
    private final String transactionAPIUrl;

    public PaymentService(WebClient.Builder webClientBuilder, @Value("${transactions.api.url:http://localhost:8080}") String transactionAPIUrl) {
        this.webClientBuilder = webClientBuilder;
        this.transactionAPIUrl = transactionAPIUrl;
    }

    public void processPayment(Payment payment) {
        validatePayment(payment);

        LOGGER.info("Processing payment for account {}: amount={}, type={}, recipient={}",
                payment.accountId(), payment.amount(), payment.paymentType(), payment.recipientName());

        Transaction transaction = convertPaymentToTransaction(payment);

        notifyTransactionService(payment, transaction);
    }

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment payload cannot be null");
        }

        if (payment.accountId() == null || payment.accountId().trim().isEmpty()) {
            throw new IllegalArgumentException("AccountId is empty or null");
        }

        try {
            Long.parseLong(payment.accountId().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("AccountId is not a valid number: " + payment.accountId());
        }

        boolean isTransfer = payment.paymentType() != null && payment.paymentType().equalsIgnoreCase("transfer");
        if (!isTransfer) {
            if (payment.paymentMethodId() == null || payment.paymentMethodId().trim().isEmpty()) {
                throw new IllegalArgumentException("paymentMethodId is empty or null for non-transfer payments");
            }
            try {
                Long.parseLong(payment.paymentMethodId().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("paymentMethodId is not a valid number: " + payment.paymentMethodId());
            }
        }
    }

    private void notifyTransactionService(Payment payment, Transaction transaction) {
        LOGGER.info("Notifying transaction API at {}/transactions/{} for payment [{}]...",
                transactionAPIUrl, payment.accountId(), payment.description());

        try {
            webClientBuilder.build()
                    .post()
                    .uri(transactionAPIUrl + "/transactions/{accountId}", payment.accountId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(transaction))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> LOGGER.info("Transaction recorded successfully: id={}", transaction.id()))
                    .doOnError(error -> LOGGER.warn("Failed to notify transaction API (offline/async): {}", error.getMessage()))
                    .onErrorReturn("Transaction recorded locally")
                    .subscribe();
        } catch (Exception ex) {
            LOGGER.warn("Could not dispatch transaction notification: {}", ex.getMessage());
        }
    }

    private Transaction convertPaymentToTransaction(Payment payment) {
        String timestamp = payment.timestamp() != null && !payment.timestamp().trim().isEmpty()
                ? payment.timestamp()
                : Instant.now().toString();

        return new Transaction(
                UUID.randomUUID().toString(),
                payment.description() != null ? payment.description() : "Payment to " + payment.recipientName(),
                "outcome",
                payment.recipientName(),
                payment.recipientBankCode(),
                payment.accountId(),
                payment.paymentType(),
                payment.amount(),
                timestamp
        );
    }
}