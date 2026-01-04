package com.microsoft.openai.samples.assistant.business.controller;

import com.microsoft.openai.samples.assistant.business.models.Account;
import com.microsoft.openai.samples.assistant.business.models.Beneficiary;
import com.microsoft.openai.samples.assistant.business.models.PaymentMethod;
import com.microsoft.openai.samples.assistant.business.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountId}")
    public Account getAccountDetails(
            @PathVariable("accountId") String accountId) {

        LOGGER.info("Fetching details for account: {}", accountId);

        Account account = accountService.getAccountDetails(accountId);

        LOGGER.info("Account details retrieved successfully for: {}", accountId);

        return account;
    }

    @GetMapping("/{accountId}/paymentmethods/{methodId}")
    public PaymentMethod getPaymentMethodDetails(
            @PathVariable("accountId") String accountId,
            @PathVariable("methodId") String methodId) {

        LOGGER.info(
                "Fetching payment method {} for account {}",
                methodId,
                accountId
        );

        PaymentMethod paymentMethod =
                accountService.getPaymentMethodDetails(methodId);

        return paymentMethod;
    }

    @GetMapping("/{accountId}/registeredBeneficiaries")
    public List<Beneficiary> getBeneficiaryDetails(
            @PathVariable("accountId") String accountId) {

        LOGGER.info("Fetching registered beneficiaries for account: {}", accountId);

        List<Beneficiary> beneficiaries =
                accountService.getRegisteredBeneficiary(accountId);

        LOGGER.info(
                "Retrieved {} beneficiary record(s) for account: {}",
                beneficiaries.size(),
                accountId
        );

        return beneficiaries;
    }
}
```
