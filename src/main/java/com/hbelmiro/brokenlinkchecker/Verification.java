package com.hbelmiro.brokenlinkchecker;

class Verification {

    private boolean verified = false;

    private String status;

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
