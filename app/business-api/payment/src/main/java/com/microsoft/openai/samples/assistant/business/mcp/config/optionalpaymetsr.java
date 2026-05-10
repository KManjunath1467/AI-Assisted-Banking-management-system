package com.microsoft.openai.samples.assistant.business.mcp.server;

import com.microsoft.openai.samples.assistant.business.models.PaymentMethod;
import com.microsoft.openai.samples.assistant.business.service.AccountService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentMethodDiagnosticsMCPService {


private final AccountService accountService;

public PaymentMethodDiagnosticsMCPService(
        AccountService accountService) {
    this.accountService = accountService;
}

/*
 * Optional MCP diagnostic tool.
 * Read-only operation; does not modify payment information.
 */
@Tool(description = "Analyze a payment method and return diagnostic information")
public PaymentMethodDiagnostic inspectPaymentMethod(
        @ToolParam(description = "Identifier of the payment method")
        String paymentMethodId) {

    if (paymentMethodId == null || paymentMethodId.isBlank()) {
        return PaymentMethodDiagnostic.invalid(
                paymentMethodId,
                "Payment method identifier is empty"
        );
    }

    PaymentMethod paymentMethod;

    try {
        paymentMethod =
                accountService.getPaymentMethodDetails(paymentMethodId);
    } catch (IllegalArgumentException exception) {
        return PaymentMethodDiagnostic.invalid(
                paymentMethodId,
                exception.getMessage()
        );
    }

    if (paymentMethod == null) {
        return PaymentMethodDiagnostic.invalid(
                paymentMethodId,
                "Payment method was not found"
        );
    }

    return createDiagnostic(paymentMethod);
}

/*
 * Optional MCP diagnostic tool.
 * Useful when several payment methods need to be inspected.
 */
@Tool(description = "Inspect multiple payment methods and return their diagnostic status")
public List<PaymentMethodDiagnostic> inspectPaymentMethods(
        @ToolParam(description = "List of payment method identifiers")
        List<String> paymentMethodIds) {

    if (paymentMethodIds == null || paymentMethodIds.isEmpty()) {
        return List.of();
    }

    List<PaymentMethodDiagnostic> results = new ArrayList<>();

    for (String paymentMethodId : paymentMethodIds) {

        if (paymentMethodId == null || paymentMethodId.isBlank()) {
            continue;
        }

        results.add(inspectPaymentMethod(paymentMethodId));
    }

    return results;
}

private PaymentMethodDiagnostic createDiagnostic(
        PaymentMethod paymentMethod) {

    int populatedFields = 0;

    if (paymentMethod.id() != null
            && !paymentMethod.id().isBlank()) {
        populatedFields++;
    }

    if (paymentMethod.type() != null
            && !paymentMethod.type().isBlank()) {
        populatedFields++;
    }

    if (paymentMethod.activationDate() != null
            && !paymentMethod.activationDate().isBlank()) {
        populatedFields++;
    }

    if (paymentMethod.expirationDate() != null
            && !paymentMethod.expirationDate().isBlank()) {
        populatedFields++;
    }

    if (paymentMethod.availableBalance() != null
            && !paymentMethod.availableBalance().isBlank()) {
        populatedFields++;
    }

    String status =
            populatedFields >= 4
                    ? "COMPLETE"
                    : "PARTIAL";

    return new PaymentMethodDiagnostic(
            paymentMethod.id(),
            paymentMethod.type(),
            status,
            populatedFields
    );
}

public record PaymentMethodDiagnostic(
        String paymentMethodId,
        String paymentType,
        String status,
        int populatedFieldCount
) {

    public static PaymentMethodDiagnostic invalid(
            String id,
            String reason) {

        return new PaymentMethodDiagnostic(
                id,
                reason,
                "INVALID",
                0
        );
    }
}


}
