
package com.microsoft.openai.samples.assistant.business.mcp.server;

import com.microsoft.openai.samples.assistant.business.models.Account;
import com.microsoft.openai.samples.assistant.business.models.Beneficiary;
import com.microsoft.openai.samples.assistant.business.models.PaymentMethod;
import com.microsoft.openai.samples.assistant.business.service.AccountService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountMCPService {

    private final AccountService accountService;

    public AccountMCPService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Tool(description = "Retrieve account information and supported payment methods")
    public Account getAccountDetails(String accountId) {
        return accountService.getAccountDetails(accountId);
    }

    @Tool(description = "Retrieve payment method details along with the available balance")
    public PaymentMethod getPaymentMethodDetails(String paymentMethodId) {
        return accountService.getPaymentMethodDetails(paymentMethodId);
    }

    @Tool(description = "Retrieve all beneficiaries registered for a given account")
    public List<Beneficiary> getRegisteredBeneficiary(String accountId) {
        return accountService.getRegisteredBeneficiary(accountId);
    }
}

