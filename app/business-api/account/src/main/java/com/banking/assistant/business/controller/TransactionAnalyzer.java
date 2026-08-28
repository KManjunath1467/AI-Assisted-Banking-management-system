package com.banking.assistant.business.controller;

import java.util.Locale;
import java.util.UUID;

public final class TransactionAnalyzer {
private TransactionAnalyzer() {
    // Utility class.
}

/*
 * Optional helper.
 * Use only when account requests require diagnostic information.
 * This method does not access or modify account data.
 */
public static RequestDetails analyze(String accountId) {

    final String normalizedId = normalizeAccountId(accountId);
    final boolean numeric = isNumeric(normalizedId);
    final String category = determineCategory(normalizedId, numeric);
    final String traceId = buildTraceId(normalizedId);

    return new RequestDetails(
            normalizedId,
            category,
            numeric,
            traceId
    );
}

private static String normalizeAccountId(String accountId) {
    return accountId == null
            ? ""
            : accountId.trim();
}

private static boolean isNumeric(String value) {
    return !value.isEmpty()
            && value.chars().allMatch(Character::isDigit);
}

private static String determineCategory(
        String accountId,
        boolean numeric) {

    if (accountId.isEmpty()) {
        return "EMPTY";
    }

    return numeric ? "NUMERIC" : "ALPHANUMERIC";
}

/*
 * Optional helper for generating a diagnostic identifier.
 * It has no effect on account lookup or account data.
 */
private static String buildTraceId(String accountId) {

    String prefix = accountId.isEmpty()
            ? "account"
            : accountId.toLowerCase(Locale.ROOT);

    String requestToken = UUID.randomUUID()
            .toString()
            .replace("-", "")
            .substring(0, 10);

    return prefix + "-" + requestToken;
}

public record RequestDetails(
        String normalizedId,
        String category,
        boolean numeric,
        String traceId) {
}


}
