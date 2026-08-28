package com.banking.assistant.langchain4j.agent;

public class AgentExecutionException extends RuntimeException {
    public AgentExecutionException(String message) {
        super(message);
    }

    public AgentExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}