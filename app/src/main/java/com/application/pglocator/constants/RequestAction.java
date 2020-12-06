package com.application.pglocator.constants;

public enum RequestAction {
    Accept("ACCEPT"),
    Reject("REJECT"),
    Pay("PAY"),
    Pending("PENDING");

    private String value;

    RequestAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
