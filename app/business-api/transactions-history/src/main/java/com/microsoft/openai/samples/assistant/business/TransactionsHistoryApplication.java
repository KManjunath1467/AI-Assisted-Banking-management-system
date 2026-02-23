// Copyright (c) Microsoft. All rights reserved.
package com.microsoft.openai.samples.assistant.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TransactionsHistoryApplication {

```
private static final Logger LOGGER =
        LoggerFactory.getLogger(TransactionsHistoryApplication.class);

public static void main(String[] args) {
    logApplicationProfile();
    SpringApplication.run(TransactionsHistoryApplication.class, args);
}

/*
 * Optional startup helper.
 * Keeps profile-related logging separate from application startup.
 */
private static void logApplicationProfile() {
    String activeProfile =
            System.getProperty("spring.profiles.active");

    LOGGER.info(
            "Application profile from system property is [{}]",
            activeProfile
    );
}
```

}
