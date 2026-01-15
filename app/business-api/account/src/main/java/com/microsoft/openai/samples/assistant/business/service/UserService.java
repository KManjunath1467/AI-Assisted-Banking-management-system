```java id="8x3nqk"
package com.microsoft.openai.samples.assistant.business.service;

import com.microsoft.openai.samples.assistant.business.models.Account;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final Map<String, Account> accounts;

    public UserService() {
        accounts = new HashMap<>();
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
        return Arrays.asList(accounts.get(userName));
    }

    /*
     * Optional helper.
     * Use only when a direct account lookup is required.
     * Existing application methods do not depend on this method.
     */
    public Account findAccount(String userName) {
        return accounts.get(userName);
    }
}
```
