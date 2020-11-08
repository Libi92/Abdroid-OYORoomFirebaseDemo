package com.application.pglocator.constants;

public enum UserState {
    ACTIVE("ACTIVE"),
    DELETED("DELETED");

    private String value;

    UserState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
