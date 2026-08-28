package com.banking.assistant.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {


private static final Logger LOGGER =
        LoggerFactory.getLogger(TransactionController.class);

private final TransactionService transactionService;

public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
}

@GetMapping("/{accountId}")
public List<Transaction> getTransactions(
        @PathVariable("accountId") String accountId,
        @RequestParam(
                name = "recipient_name",
                required = false
        ) String recipientName) {

    LOGGER.info(
            "Fetching transactions for account [{}]. Recipient filter: [{}]",
            accountId,
            recipientName
    );

    boolean hasRecipientFilter =
            recipientName != null && !recipientName.isBlank();

    if (hasRecipientFilter) {
        return transactionService.getTransactionsByRecipientName(
                accountId,
                recipientName
        );
    }

    return transactionService.getlastTransactions(accountId);
}

@PostMapping("/{accountId}")
public void notifyTransaction(
        @PathVariable("accountId") String accountId,
        @RequestBody Transaction transaction) {

    LOGGER.info(
            "Received transaction notification for account [{}]: {}",
            accountId,
            transaction
    );

    transactionService.notifyTransaction(accountId, transaction);
}

/*
 * Optional helper.
 * Use only when transaction filtering needs to be checked
 * independently in future controller methods.
 */
private boolean hasRecipientFilter(String recipientName) {
    return recipientName != null && !recipientName.isBlank();
}


}
