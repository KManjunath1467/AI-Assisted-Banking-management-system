
package com.banking.assistant.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Transaction(
        @JsonProperty("id")
        String id,

        @JsonProperty("description")
        String description,

        // Represents whether the transaction is an income or outcome.
        @JsonProperty("type")
        String type,

        @JsonProperty("recipientName")
        String recipientName,

        @JsonProperty("recipientBankReference")
        String recipientBankReference,

        @JsonProperty("accountId")
        String accountId,

        @JsonProperty("paymentType")
        String paymentType,

        @JsonProperty("amount")
        String amount,

        @JsonProperty("timestamp")
        String timestamp
) {

    /*
     * Optional helper.
     * Use only when a transaction needs a compact display description.
     * Does not modify the stored transaction data.
     */
    public String getDisplaySummary() {
        String recipient = recipientName == null ? "Unknown recipient" : recipientName;
        String transactionAmount = amount == null ? "Unknown amount" : amount;

        return recipient + " - " + transactionAmount;
    }

    /*
     * Optional helper.
     * Useful when checking whether the transaction has
     * the minimum identifying information available.
     */
    public boolean hasBasicDetails() {
        return id != null
                && !id.isBlank()
                && accountId != null
                && !accountId.isBlank();
    }
}
