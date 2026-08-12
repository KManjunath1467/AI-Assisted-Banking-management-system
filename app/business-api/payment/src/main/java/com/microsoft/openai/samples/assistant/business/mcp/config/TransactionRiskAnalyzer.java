package com.microsoft.openai.samples.assistant.business.analysis;

import com.microsoft.openai.samples.assistant.business.models.Account;
import com.microsoft.openai.samples.assistant.business.models.PaymentMethodSummary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class TransactionRiskAnalyzer {


private static final int LOW_RISK_THRESHOLD = 20;
private static final int MEDIUM_RISK_THRESHOLD = 50;

private TransactionRiskAnalyzer() {
    // Utility class.
}

/*
 * Optional analysis utility.
 *
 * This class is read-only. It does not modify the supplied
 * Account object or interact with the database.
 */
public static RiskReport analyze(Account account) {

    if (account == null) {
        return new RiskReport(
                "UNKNOWN",
                100,
                List.of("Account information is unavailable")
        );
    }

    List<RiskFactor> factors = new ArrayList<>();

    evaluateIdentity(account, factors);
    evaluateBalance(account, factors);
    evaluateCurrency(account, factors);
    evaluatePaymentMethods(account, factors);

    int score = calculateScore(factors);
    String level = determineRiskLevel(score);

    List<String> findings = factors.stream()
            .filter(RiskFactor::hasImpact)
            .sorted(Comparator.comparingInt(RiskFactor::weight).reversed())
            .map(RiskFactor::description)
            .toList();

    return new RiskReport(level, score, findings);
}

private static void evaluateIdentity(
        Account account,
        List<RiskFactor> factors) {

    if (isBlank(account.id())) {
        factors.add(new RiskFactor(
                "Missing account identifier",
                30
        ));
    }

    if (isBlank(account.userName())) {
        factors.add(new RiskFactor(
                "Missing user identifier",
                20
        ));
    }

    if (isBlank(account.accountHolderFullName())) {
        factors.add(new RiskFactor(
                "Account holder name is unavailable",
                15
        ));
    }
}

private static void evaluateBalance(
        Account account,
        List<RiskFactor> factors) {

    if (isBlank(account.balance())) {
        factors.add(new RiskFactor(
                "Account balance is unavailable",
                20
        ));
        return;
    }

    try {
        double balance = Double.parseDouble(account.balance());

        if (balance < 0) {
            factors.add(new RiskFactor(
                    "Account balance is negative",
                    40
            ));
        } else if (balance == 0) {
            factors.add(new RiskFactor(
                    "Account balance is zero",
                    10
            ));
        }

    } catch (NumberFormatException exception) {
        factors.add(new RiskFactor(
                "Account balance has an invalid numeric format",
                25
        ));
    }
}

private static void evaluateCurrency(
        Account account,
        List<RiskFactor> factors) {

    if (isBlank(account.currency())) {
        factors.add(new RiskFactor(
                "Account currency is unavailable",
                15
        ));
    }
}

private static void evaluatePaymentMethods(
        Account account,
        List<RiskFactor> factors) {

    List<PaymentMethodSummary> methods =
            account.paymentMethods();

    if (methods == null || methods.isEmpty()) {
        factors.add(new RiskFactor(
                "No payment methods are associated with the account",
                15
        ));
        return;
    }

    long incompleteMethods = methods.stream()
            .filter(Objects::nonNull)
            .filter(method ->
                    isBlank(method.id())
                            || isBlank(method.type()))
            .count();

    if (incompleteMethods > 0) {
        factors.add(new RiskFactor(
                incompleteMethods
                        + " payment method(s) contain incomplete information",
                20
        ));
    }
}

private static int calculateScore(
        List<RiskFactor> factors) {

    return Math.min(
            100,
            factors.stream()
                    .mapToInt(RiskFactor::weight)
                    .sum()
    );
}

private static String determineRiskLevel(int score) {

    if (score >= MEDIUM_RISK_THRESHOLD) {
        return "HIGH";
    }

    if (score >= LOW_RISK_THRESHOLD) {
        return "MEDIUM";
    }

    return "LOW";
}

private static boolean isBlank(String value) {
    return value == null || value.isBlank();
}

private record RiskFactor(
        String description,
        int weight
) {

    private boolean hasImpact() {
        return weight > 0;
    }
}

public record RiskReport(
        String riskLevel,
        int score,
        List<String> findings
) {
}


}
