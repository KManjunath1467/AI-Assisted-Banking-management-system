// Copyright (c) Microsoft. All rights reserved.
package com.banking.assistant.controller;

import java.util.List;

public record ResponseContext(String thoughts, List<String> data_points) {}
