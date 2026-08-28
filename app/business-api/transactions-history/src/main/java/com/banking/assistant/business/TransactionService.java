package com.banking.assistant.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);

    private final Map<String, List<Transaction>> lastTransactions = new ConcurrentHashMap<>();
    private final Map<String, List<Transaction>> allTransactions = new ConcurrentHashMap<>();

    public TransactionService() {
        initializeTransactions();
    }

    private void initializeTransactions() {
        // Account 1000 (Alice User)
        List<Transaction> list1000 = new CopyOnWriteArrayList<>(Arrays.asList(
                new Transaction("tx-1001", "Payment of electricity bill #9921", "outcome", "Acme Power", "0001", "1000", "BankTransfer", "145.50", "2024-04-10T10:15:00Z"),
                new Transaction("tx-1002", "Grocery shopping supermarket", "outcome", "Whole Foods", "0002", "1000", "CreditCard", "84.20", "2024-04-08T16:30:00Z"),
                new Transaction("tx-1003", "Internet Fiber Subscription", "outcome", "Contoso Telecom", "0003", "1000", "DirectDebit", "65.00", "2024-04-01T09:00:00Z"),
                new Transaction("tx-1004", "Salary Deposit", "income", "Tech Corp", "0004", "1000", "Transfer", "3500.00", "2024-03-30T08:00:00Z"),
                new Transaction("tx-1005", "Coffee & Bakery", "outcome", "Starbucks", "0005", "1000", "CreditCard", "12.75", "2024-03-28T11:20:00Z")
        ));
        allTransactions.put("1000", list1000);
        lastTransactions.put("1000", new CopyOnWriteArrayList<>(list1000.subList(0, Math.min(5, list1000.size()))));

        // Account 1010 (Bob User)
        List<Transaction> list1010 = new CopyOnWriteArrayList<>(Arrays.asList(
                new Transaction("11", "Payment of the bill 334398", "outcome", "acme", "0001", "1010", "BankTransfer", "100.00", "2024-04-01T12:00:00Z"),
                new Transaction("22", "Payment of the bill 4613", "outcome", "contoso", "0002", "1010", "CreditCard", "200.00", "2024-03-02T12:00:00Z"),
                new Transaction("33", "Payment of the bill 724563", "outcome", "duff", "0003", "1010", "BankTransfer", "300.00", "2023-10-03T12:00:00Z"),
                new Transaction("43", "Payment of the bill 8898943", "outcome", "wayne enterprises", "0004", "1010", "DirectDebit", "400.00", "2023-08-04T12:00:00Z"),
                new Transaction("53", "Payment of the bill 19dee", "outcome", "oscorp", "0005", "1010", "BankTransfer", "500.00", "2023-04-05T12:00:00Z"),
                new Transaction("12", "Payment of the bill 5517", "outcome", "contoso", "0001", "1010", "CreditCard", "100.00", "2024-03-01T12:00:00Z"),
                new Transaction("21", "Payment of the bill 4200", "outcome", "acme", "0002", "1010", "BankTransfer", "200.00", "2024-01-02T12:00:00Z")
        ));
        allTransactions.put("1010", list1010);
        lastTransactions.put("1010", new CopyOnWriteArrayList<>(list1010.subList(0, Math.min(5, list1010.size()))));

        // Account 1020 (Charlie User)
        List<Transaction> list1020 = new CopyOnWriteArrayList<>(Arrays.asList(
                new Transaction("tx-2001", "Monthly Apartment Rent", "outcome", "Metropolitan Properties", "0001", "1020", "BankTransfer", "1200.00", "2024-04-01T10:00:00Z"),
                new Transaction("tx-2002", "Online Cloud Subscription", "outcome", "Cloud Provider", "0002", "1020", "CreditCard", "45.00", "2024-03-15T14:20:00Z"),
                new Transaction("tx-2003", "Consulting Fee Deposit", "income", "Acme Consulting", "0003", "1020", "Transfer", "2200.00", "2024-03-01T09:00:00Z")
        ));
        allTransactions.put("1020", list1020);
        lastTransactions.put("1020", new CopyOnWriteArrayList<>(list1020.subList(0, Math.min(5, list1020.size()))));
    }

    public List<Transaction> getTransactionsByRecipientName(String accountId, String name) {
        validateAccountId(accountId);
        List<Transaction> list = allTransactions.get(accountId);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>(list);
        }
        String lowerName = name.trim().toLowerCase(Locale.ROOT);
        return list.stream()
                .filter(transaction -> transaction.recipientName() != null &&
                        transaction.recipientName().toLowerCase(Locale.ROOT).contains(lowerName))
                .collect(Collectors.toList());
    }

    public List<Transaction> getlastTransactions(String accountId) {
        validateAccountId(accountId);
        List<Transaction> list = lastTransactions.get(accountId);
        if (list == null) {
            return allTransactions.getOrDefault(accountId, Collections.emptyList());
        }
        return new ArrayList<>(list);
    }

    public List<Transaction> getAllTransactions(String accountId) {
        validateAccountId(accountId);
        return new ArrayList<>(allTransactions.getOrDefault(accountId, Collections.emptyList()));
    }

    public void notifyTransaction(String accountId, Transaction transaction) {
        validateAccountId(accountId);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }

        allTransactions.computeIfAbsent(accountId, k -> new CopyOnWriteArrayList<>()).add(0, transaction);
        
        List<Transaction> lastList = lastTransactions.computeIfAbsent(accountId, k -> new CopyOnWriteArrayList<>());
        lastList.add(0, transaction);
        if (lastList.size() > 10) {
            lastList.remove(lastList.size() - 1);
        }

        LOGGER.info("Transaction recorded for account {}: id={}, amount={}, recipient={}",
                accountId, transaction.id(), transaction.amount(), transaction.recipientName());
    }

    private void validateAccountId(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("AccountId is empty or null");
        }
        try {
            Long.parseLong(accountId.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("AccountId is not a valid number: " + accountId);
        }
    }
}
