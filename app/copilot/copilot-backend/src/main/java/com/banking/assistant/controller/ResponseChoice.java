// Copyright (c) Microsoft. All rights reserved.
package com.banking.assistant.controller;

public record ResponseChoice(
        int index, ResponseMessage message, ResponseContext context, ResponseMessage delta) {}
