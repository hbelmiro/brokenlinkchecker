package com.hbelmiro.brokenlinkchecker;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.function.Consumer;

@ApplicationScoped
class BrokenLinkChecker {

    @ConfigProperty(name = "com.hbelmiro.brokenlinkchecker.ignored-urls")
    List<String> ignoredUrls;

    public void check(String rootContext, String page, VerificationOptions verificationOptions, Consumer<String> onCheckStatus) {
        var verificationQueue = new VerificationQueue(ignoredUrls, rootContext, page, onCheckStatus);
        verificationQueue.verify(verificationOptions);
    }
}
