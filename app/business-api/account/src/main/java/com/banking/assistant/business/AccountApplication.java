
package com.banking.assistant.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccountApplication {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AccountApplication.class);

    public static void main(String[] args) {
        logActiveProfile();
        SpringApplication.run(AccountApplication.class, args);
    }

    /*
     * Optional helper.
     * Keeps startup logging separate from application bootstrapping.
     */
    private static void logActiveProfile() {
        String activeProfile = System.getProperty("spring.profiles.active");

        LOGGER.info(
                "Application profile from system property is [{}]",
                activeProfile
        );
    }
}

