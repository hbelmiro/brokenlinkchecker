package com.hbelmiro.brokenlinkchecker;

class VerificationOptions {

    private final boolean showOnlyErrors;

    static VerificationOptionsBuilder newBuilder() {
        return new VerificationOptionsBuilder();
    }

    private VerificationOptions(VerificationOptionsBuilder builder) {
        this.showOnlyErrors = builder.showOnlyErrors;
    }

    public boolean isShowOnlyErrors() {
        return showOnlyErrors;
    }

    static class VerificationOptionsBuilder {

        private boolean showOnlyErrors = false;

        private VerificationOptionsBuilder() {
        }

        VerificationOptionsBuilder showOnlyErrors(boolean showOnlyErrors) {
            this.showOnlyErrors = showOnlyErrors;
            return this;
        }

        VerificationOptions build() {
            return new VerificationOptions(this);
        }
    }
}
