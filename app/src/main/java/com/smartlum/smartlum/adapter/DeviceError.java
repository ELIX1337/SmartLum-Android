package com.smartlum.smartlum.adapter;

public class DeviceError {

    private final int register;
    private final int errorState;
    private String errorDescription;

    public DeviceError(final int[] data) {
        register = data[0];
        errorState = data[1];
    }

    public int getRegister() {
        return register;
    }

    public int getCode() {
        return errorState;
    }

    public boolean onError() {
        return errorState == 1;
    }

    public void setErrorDescription(final String description) {
        errorDescription = description;
    }

    public String getErrorDescription() {
        return (errorDescription != null ? errorDescription : "null");
    }
}
