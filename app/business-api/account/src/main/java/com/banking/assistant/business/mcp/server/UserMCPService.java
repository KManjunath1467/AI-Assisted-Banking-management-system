
package com.banking.assistant.business.mcp.server;

import com.banking.assistant.business.models.Account;
import com.banking.assistant.business.service.UserService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMCPService {

    private final UserService userService;

    public UserMCPService(UserService userService) {
        this.userService = userService;
    }

    @Tool(description = "Retrieve all accounts associated with a specific user")
    public List<Account> getAccountsByUserName(
            @ToolParam(description = "Username of the logged-in user")
            String userName) {

        return userService.getAccountsByUserName(userName);
    }
}

