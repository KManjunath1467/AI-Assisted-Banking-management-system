package com.microsoft.openai.samples.assistant.config;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.identity.AzureCliCredentialBuilder; 
import com.azure.identity.EnvironmentCredentialBuilder; 
import com.azure.identity.ManagedIdentityCredentialBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean; 
import org.springframework.context.annotation.Configuration; 
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Configuration
public class AzureAuthenticationConfiguration { 

    @Value("${azure.identity.client-id:system-managed-identity}") 
    String clientId; 

    @Profile("dev")
    @Bean
    public TokenCredential localTokenCredential() {
        try {
            return new AzureCliCredentialBuilder().build();
        } catch (Exception e) {
            return dummyTokenCredential();
        }
    }

    @Profile("docker")
    @Bean
    public TokenCredential servicePrincipalTokenCredential() {
        try {
            return new EnvironmentCredentialBuilder().build();
        } catch (Exception e) {
            return dummyTokenCredential();
        }
    }

    @Bean
    @Profile("azure")
    public TokenCredential managedIdentityTokenCredential() {
        if (this.clientId.equals("system-managed-identity"))
            return new ManagedIdentityCredentialBuilder().build();
        else 
            return new ManagedIdentityCredentialBuilder().clientId(this.clientId).build();
    }

    @Bean
    @ConditionalOnMissingBean(TokenCredential.class)
    public TokenCredential defaultFallbackTokenCredential() {
        return dummyTokenCredential();
    }

    private TokenCredential dummyTokenCredential() {
        return request -> Mono.just(new AccessToken("dummy-token", OffsetDateTime.now().plusHours(24)));
    }
}
