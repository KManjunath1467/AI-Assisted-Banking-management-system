```java
package com.microsoft.openai.samples.assistant.business.controller;

import com.microsoft.openai.samples.assistant.business.models.Account;
import com.microsoft.openai.samples.assistant.business.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userName}/accounts")
    public List<Account> getAccountsByUserName(
            @PathVariable("userName") String userName) {

        LOGGER.info("Fetching accounts for user: {}", userName);

        List<Account> accounts = userService.getAccountsByUserName(userName);

        LOGGER.info("Retrieved {} account(s) for user: {}",
                accounts.size(), userName);

        return accounts;
    }
}
```
