package com.hbelmiro.brokenlinkchecker;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.function.Consumer;

@ApplicationScoped
class BrokenLinkChecker {

    public void check(String rootContext, String page, VerificationOptions verificationOptions, Consumer<String> onCheckStatus) {
        var verificationQueue = new VerificationQueue(rootContext, page, onCheckStatus);
        verificationQueue.verify(verificationOptions);
    }
}
