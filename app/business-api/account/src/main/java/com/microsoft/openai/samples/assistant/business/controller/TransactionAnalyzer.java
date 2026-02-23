package com.microsoft.openai.samples.assistant.business.controller;

import java.util.Locale;
import java.util.UUID;

public final class AccountRequestAnalyzer {

```
private AccountRequestAnalyzer() {
    // Utility class.
}

/*
 * Optional helper.
 * Use only when account requests require diagnostic information.
 * This method does not access or modify account data.
 */
public static RequestDetails analyze(String accountId) {

    String normalizedId =
            accountId == null
                    ? ""
                    : accountId.trim();

    boolean numeric =
            !normalizedId.isEmpty()
                    && normalizedId.chars().allMatch(Character::isDigit);

    String category;

    if (normalizedId.isEmpty()) {
        category = "EMPTY";
    } else if (numeric) {
        category = "NUMERIC";
    } else {
        category = "ALPHANUMERIC";
    }

    String traceId = buildTraceId(normalizedId);

    return new RequestDetails(
            normalizedId,
            category,
            numeric,
            traceId
    );
}

/*
 * Optional helper for generating a diagnostic identifier.
 * It has no effect on the account lookup itself.
 */
private static String buildTraceId(String accountId) {

    String prefix =
            accountId.isEmpty()
                    ? "account"
                    : accountId.toLowerCase(Locale.ROOT);

    String requestToken =
            UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 10);

    return prefix + "-" + requestToken;
}

public record RequestDetails(
        String normalizedId,
        String category,
        boolean numeric,
        String traceId
) {
}
```

}
