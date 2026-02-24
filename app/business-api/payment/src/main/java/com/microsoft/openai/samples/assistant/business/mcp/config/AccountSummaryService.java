package com.microsoft.openai.samples.assistant.business.service;

import com.microsoft.openai.samples.assistant.business.models.Account;
import com.microsoft.openai.samples.assistant.business.models.PaymentMethodSummary;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AccountSummaryService {

```
private final AccountService accountService;

public AccountSummaryService(AccountService accountService) {
    this.accountService = accountService;
}

/*
 * Optional business operation.
 * Use only when a summarized view of an account is required.
 *
 * This operation is read-only and does not modify account data.
 */
public AccountSummary generateSummary(String accountId) {

    Account account = accountService.getAccountDetails(accountId);

    if (account == null) {
        return AccountSummary.notFound(accountId);
    }

    List<PaymentMethodSummary> paymentMethods =
            account.paymentMethods() == null
                    ? Collections.emptyList()
                    : account.paymentMethods();

    int methodCount = paymentMethods.size();

    long activeMethods = paymentMethods.stream()
            .filter(method -> method != null)
            .filter(this::hasValidDates)
            .count();

    return new AccountSummary(
            account.id(),
            account.accountHolderFullName(),
            account.currency(),
            methodCount,
            (int) activeMethods,
            true
    );
}

/*
 * Optional helper for determining whether a payment-method
 * summary contains its expected date information.
 */
private boolean hasValidDates(PaymentMethodSummary method) {

    return method.activationDate() != null
            && !method.activationDate().isBlank()
            && method.expirationDate() != null
            && !method.expirationDate().isBlank();
}

public record AccountSummary(
        String accountId,
        String accountHolder,
        String currency,
        int paymentMethodCount,
        int methodsWithValidDates,
        boolean accountFound
) {

    private static AccountSummary notFound(String accountId) {
        return new AccountSummary(
                accountId,
                null,
                null,
                0,
                0,
                false
        );
    }
}
```

}
