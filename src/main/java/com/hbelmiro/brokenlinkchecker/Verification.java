package com.hbelmiro.brokenlinkchecker;

class Verification {

    private final String url;

    private boolean verified = false;

    private Integer statusCode = null;

    Verification(String url) {
        this.url = url;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setStatusCode(Integer statusCode) {
        this.verified = true;
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
