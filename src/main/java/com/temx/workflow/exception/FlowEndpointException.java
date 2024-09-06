package com.temx.workflow.exception;


public class FlowEndpointException extends RuntimeException {
    private final int statusCode;

    public FlowEndpointException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}