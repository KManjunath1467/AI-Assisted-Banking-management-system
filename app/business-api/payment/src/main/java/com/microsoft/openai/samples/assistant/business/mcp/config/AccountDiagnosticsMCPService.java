package com.microsoft.openai.samples.assistant.business.mcp.server;

import com.microsoft.openai.samples.assistant.business.models.Account;
import com.microsoft.openai.samples.assistant.business.service.AccountService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PaymentDiagnosticsMCPService {


private final PaymentService paymentService;

public PaymentDiagnosticsMCPService(PaymentService paymentService) {
    this.paymentService = paymentService;
}

/*
 * Optional MCP tool.
 *
 * This is read-only and does not modify accounts.
 * Use only when account diagnostics are required.
 */
@Tool(description = "Analyze an account and return non-sensitive diagnostic information")
public AccountDiagnosticResult analyzeAccount(
        @ToolParam(description = "Identifier of the account to analyze")
        String accountId) {

    Account account = accountService.getAccountDetails(accountId);

    if (account == null) {
        return new AccountDiagnosticResult(
                accountId,
                false,
                "Account was not found",
                0,
                0
        );
    }

    int paymentMethodCount =
            account.paymentMethods() == null
                    ? 0
                    : account.paymentMethods().size();

    int populatedFields = countPopulatedFields(account);

    String status = determineAccountStatus(account);

    return new AccountDiagnosticResult(
            account.id(),
            true,
            status,
            paymentMethodCount,
            populatedFields
    );
}

/*
 * Optional MCP tool.
 *
 * Produces a summary for multiple account identifiers.
 * It does not change account data.
 */
@Tool(description = "Generate diagnostic summaries for multiple accounts")
public List<AccountDiagnosticResult> analyzeAccounts(
        @ToolParam(description = "Account identifiers to analyze")
        List<String> accountIds) {

    if (accountIds == null || accountIds.isEmpty()) {
        return List.of();
    }

    List<AccountDiagnosticResult> results = new ArrayList<>();

    for (String accountId : accountIds) {
        if (accountId == null || accountId.isBlank()) {
            continue;
        }

        results.add(analyzeAccount(accountId));
    }

    return results.stream()
            .sorted(
                    Comparator.comparing(
                            AccountDiagnosticResult::accountFound
                    ).reversed()
            )
            .toList();
}

private int countPopulatedFields(Account account) {

    int count = 0;

    if (account.id() != null && !account.id().isBlank()) {
        count++;
    }

    if (account.userName() != null && !account.userName().isBlank()) {
        count++;
    }

    if (account.accountHolderFullName() != null
            && !account.accountHolderFullName().isBlank()) {
        count++;
    }

    if (account.currency() != null && !account.currency().isBlank()) {
        count++;
    }

    if (account.activationDate() != null
            && !account.activationDate().isBlank()) {
        count++;
    }

    if (account.balance() != null && !account.balance().isBlank()) {
        count++;
    }

    if (account.paymentMethods() != null) {
        count++;
    }

    return count;
}

private String determineAccountStatus(Account account) {

    if (account.id() == null || account.id().isBlank()) {
        return "INCOMPLETE";
    }

    if (account.currency() == null || account.currency().isBlank()) {
        return "MISSING_CURRENCY";
    }

    if (account.balance() == null || account.balance().isBlank()) {
        return "MISSING_BALANCE";
    }

    return "READY";
}

/*
 * MCP response model.
 * This is intentionally separate from Account so the original
 * Account model remains unchanged.
 */
public record AccountDiagnosticResult(
        String accountId,
        boolean accountFound,
        String status,
        int paymentMethodCount,
        int populatedFieldCount
) {
}


}
