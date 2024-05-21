package com.hbelmiro.brokenlinkchecker;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.concurrent.Flow;

@ApplicationScoped
class BrokenLinkChecker {

    public Flow.Publisher<String> check(String rootContext, String page, VerificationOptions verificationOptions) {
        var verificationQueue = new VerificationQueue(rootContext, page);
        return verificationQueue.verify(verificationOptions);
    }
}
