package com.microsoft.openai.samples.assistant.business.controller;

import java.util.Locale;
import java.util.regex.Pattern;

public final class AccountIdentifierUtility {

```
private static final Pattern ACCOUNT_ID_PATTERN =
        Pattern.compile("^[A-Za-z0-9_-]{3,32}$");

private AccountIdentifierUtility() {
    // Prevent instantiation of this utility class.
}

/*
 * Optional helper.
 * Use only when an account identifier needs to be
 * checked before being passed to another component.
 */
public static boolean isValid(String accountId) {

    if (accountId == null) {
        return false;
    }

    String normalizedId = accountId.trim();

    return ACCOUNT_ID_PATTERN.matcher(normalizedId).matches();
}

/*
 * Optional helper.
 * Produces a normalized representation for logging or
 * diagnostic purposes without changing the original value.
 */
public static String normalize(String accountId) {

    if (accountId == null) {
        return "";
    }

    return accountId
            .trim()
            .toLowerCase(Locale.ROOT);
}

/*
 * Optional helper.
 * Masks part of an account identifier when it needs
 * to be displayed in diagnostic output.
 */
public static String mask(String accountId) {

    String normalizedId = normalize(accountId);

    if (normalizedId.length() <= 4) {
        return normalizedId;
    }

    int visibleCharacters = 4;
    int hiddenCharacters = normalizedId.length() - visibleCharacters;

    return "*".repeat(hiddenCharacters)
            + normalizedId.substring(
                    normalizedId.length() - visibleCharacters
            );
}
```

}
