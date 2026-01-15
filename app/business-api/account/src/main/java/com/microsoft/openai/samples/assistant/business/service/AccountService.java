```java
package com.microsoft.openai.samples.assistant.business.service;

import com.microsoft.openai.samples.assistant.business.models.Account;
import com.microsoft.openai.samples.assistant.business.models.Beneficiary;
import com.microsoft.openai.samples.assistant.business.models.PaymentMethod;
import com.microsoft.openai.samples.assistant.business.models.PaymentMethodSummary;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {

    private final Map<String, Account> accounts;
    private final Map<String, PaymentMethod> paymentMethods;

    public AccountService() {
        accounts = new HashMap<>();
        paymentMethods = new HashMap<>();

        initializeAccounts();
        initializePaymentMethods();
    }

    private void initializeAccounts() {

        accounts.put(
                "1000",
                new Account(
                        "1000",
                        "alice.user@contoso.com",
                        "Alice User",
                        "USD",
                        "2022-01-01",
                        "5000",
                        Arrays.asList(
                                new PaymentMethodSummary(
                                        "12345",
                                        "Visa",
                                        "2022-01-01",
                                        "2025-01-01"
                                ),
                                new PaymentMethodSummary(
                                        "23456",
                                        "BankTransfer",
                                        "2022-01-01",
                                        "9999-01-01"
                                )
                        )
                )
        );

        accounts.put(
                "1010",
                new Account(
                        "1010",
                        "bob.user@contoso.com",
                        "Bob User",
                        "EUR",
                        "2022-01-01",
                        "10000",
                        Arrays.asList(
                                new PaymentMethodSummary(
                                        "345678",
                                        "BankTransfer",
                                        "2022-01-01",
                                        "9999-01-01"
                                ),
                                new PaymentMethodSummary(
                                        "55555",
                                        "Visa",
                                        "2022-01-01",
                                        "2026-01-01"
                                )
                        )
                )
        );

        accounts.put(
                "1020",
                new Account(
                        "1020",
                        "charlie.user@contoso.com",
                        "Charlie User",
                        "EUR",
                        "2022-01-01",
                        "3000",
                        Arrays.asList(
                                new PaymentMethodSummary(
                                        "46748576",
                                        "DirectDebit",
                                        "2022-02-01",
                                        "9999-02-01"
                                )
                        )
                )
        );
    }

    private void initializePaymentMethods() {

        paymentMethods.put(
                "12345",
                new PaymentMethod(
                        "12345",
                        "Visa",
                        "2022-01-01",
                        "2025-01-01",
                        "500.00",
                        "1234567812345678"
                )
        );

        paymentMethods.put(
                "55555",
                new PaymentMethod(
                        "55555",
                        "Visa",
                        "2024-01-01",
                        "2028-01-01",
                        "350.00",
                        "637362551913266"
                )
        );

        paymentMethods.put(
                "23456",
                new PaymentMethod(
                        "23456",
                        "BankTransfer",
                        "2022-01-01",
                        "9999-01-01",
                        "5000.00",
                        null
                )
        );

        paymentMethods.put(
                "345678",
                new PaymentMethod(
                        "345678",
                        "BankTransfer",
                        "2022-01-01",
                        "9999-01-01",
                        "10000.00",
                        null
                )
        );
    }

    public Account getAccountDetails(String accountId) {
        validateNumericId(accountId, "AccountId");
        return accounts.get(accountId);
    }

    public PaymentMethod getPaymentMethodDetails(String paymentMethodId) {
        validateNumericId(paymentMethodId, "AccountId");
        return paymentMethods.get(paymentMethodId);
    }

    public List<Beneficiary> getRegisteredBeneficiary(String accountId) {
        validateNumericId(accountId, "AccountId");

        return Arrays.asList(
                new Beneficiary(
                        "1",
                        "Mike ThePlumber",
                        "123456789",
                        "Intesa Sanpaolo"
                ),
                new Beneficiary(
                        "2",
                        "Jane TheElectrician",
                        "987654321",
                        "UBS"
                )
        );
    }

    /*
     * Shared validation helper.
     * Keeps the existing validation behavior in one place.
     * It can also be reused by future account-related methods.
     */
    private void validateNumericId(String id, String fieldName) {

        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " is empty or null"
            );
        }

        try {
            Integer.parseInt(id);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    fieldName + " is not a valid number"
            );
        }
    }
}
```
