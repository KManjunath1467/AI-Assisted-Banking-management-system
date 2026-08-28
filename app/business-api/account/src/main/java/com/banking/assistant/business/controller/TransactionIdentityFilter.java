package com.banking.assistant.business.controller;

import java.util.Locale;
import java.util.UUID;

public final class TransactionIdentityFilter {

private static final String DEFAULT_CATEGORY = "UNKNOWN";

private TransactionIdentityFilter() {
    // Utility class.
}

/*
 * Optional helper.
 * Use only when a request needs lightweight account-ID analysis.
 * This method does not access or modify account data.
 */
public static RequestDetails analyze(String accountId) {

    String normalizedId = normalize(accountId);
    String category = classify(normalizedId);

    return new RequestDetails(
            normalizedId,
            category,
            normalizedId.length(),
            createRequestId()
    );
}

private static String normalize(String value) {
    if (value == null) {
        return "";
    }

    return value.trim();
}

private static String classify(String value) {
    if (value.isEmpty()) {
        return "EMPTY";
    }

    if (value.chars().allMatch(Character::isDigit)) {
        return "NUMERIC";
    }

    if (value.chars().allMatch(Character::isLetterOrDigit)) {
        return "ALPHANUMERIC";
    }

    return DEFAULT_CATEGORY;
}

/*
 * Optional helper.
 * Generates a short identifier that can be used when tracing
 * diagnostic operations in logs.
 */
private static String createRequestId() {
    return UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, 8)
            .toUpperCase(Locale.ROOT);
}

/*
 * Optional helper.
 * Use when displaying an account identifier in diagnostic output.
 * The original value is not modified.
 */
public static String mask(String accountId) {

    String value = normalize(accountId);

    if (value.length() <= 4) {
        return value;
    }

    int visibleCharacters = 4;
    int hiddenCharacters = value.length() - visibleCharacters;

    return "*".repeat(hiddenCharacters)
            + value.substring(hiddenCharacters);
}

public record RequestDetails(
        String normalizedId,
        String category,
        int length,
        String requestId) {
}


}
