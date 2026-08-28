package com.banking.assistant.business.service;

import com.banking.assistant.business.models.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public UserService() {
        initializeUsers();
    }

    private void initializeUsers() {
        accounts.put(
                "alice.user@contoso.com",
                new Account(
                        "1000",
                        "alice.user@contoso.com",
                        "Alice User",
                        "USD",
                        "2022-01-01",
                        "5000",
                        null
                )
        );

        accounts.put(
                "bob.user@contoso.com",
                new Account(
                        "1010",
                        "bob.user@contoso.com",
                        "Bob User",
                        "EUR",
                        "2022-01-01",
                        "10000",
                        null
                )
        );

        accounts.put(
                "charlie.user@contoso.com",
                new Account(
                        "1020",
                        "charlie.user@contoso.com",
                        "Charlie User",
                        "EUR",
                        "2022-01-01",
                        "3000",
                        null
                )
        );
    }

    public List<Account> getAccountsByUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            LOGGER.warn("Attempted to lookup accounts with empty username");
            return Collections.emptyList();
        }
        Account account = accounts.get(userName.trim().toLowerCase());
        if (account == null) {
            // fallback check ignoring case
            account = accounts.values().stream()
                    .filter(a -> a.userName().equalsIgnoreCase(userName.trim()))
                    .findFirst()
                    .orElse(null);
        }
        return account != null ? Collections.singletonList(account) : Collections.emptyList();
    }

    public Account findAccount(String userName) {
        if (userName == null) {
            return null;
        }
        return accounts.get(userName.trim().toLowerCase());
    }
}
