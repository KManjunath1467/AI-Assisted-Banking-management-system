```java
package com.microsoft.openai.samples.assistant.business.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Beneficiary(
        @JsonProperty("id")
        String id,

        @JsonProperty("fullName")
        String fullName,

        @JsonProperty("bankCode")
        String bankCode,

        @JsonProperty("bankName")
        String bankName
) {

    /**
     * Returns a readable representation of the beneficiary's bank.
     */
    public String getBankSummary() {
        if (bankName == null || bankName.isBlank()) {
            return bankCode;
        }

        if (bankCode == null || bankCode.isBlank()) {
            return bankName;
        }

        return bankName + " (" + bankCode + ")";
    }

    /**
     * Indicates whether the beneficiary contains the basic
     * identifying information required by the application.
     */
    public boolean hasBasicDetails() {
        return id != null
                && !id.isBlank()
                && fullName != null
                && !fullName.isBlank();
    }
}
```
