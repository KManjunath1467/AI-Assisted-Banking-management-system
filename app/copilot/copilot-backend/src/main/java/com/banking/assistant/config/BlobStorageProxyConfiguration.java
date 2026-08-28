// Copyright (c) Microsoft. All rights reserved.
package com.banking.assistant.config;

import com.azure.core.credential.TokenCredential;
import com.banking.assistant.proxy.BlobStorageProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlobStorageProxyConfiguration {
    @Value("${storage-account.service:storage}")
    String storageAccountServiceName;
    @Value("${blob.container.name:content}")
    String containerName;

    @Bean
    public BlobStorageProxy blobStorageProxy(TokenCredential tokenCredential) {
            return new BlobStorageProxy(storageAccountServiceName,containerName,tokenCredential);
    }

}
