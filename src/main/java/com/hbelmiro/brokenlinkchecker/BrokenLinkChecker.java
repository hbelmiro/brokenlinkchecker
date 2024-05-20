package com.hbelmiro.brokenlinkchecker;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
class BrokenLinkChecker {

    private final WebCrawler webCrawler;

    @Inject
    BrokenLinkChecker(WebCrawler webCrawler) {
        this.webCrawler = webCrawler;
    }

    public List<String> check(String domain) {
        var verificationQueue = new VerificationQueue(domain);
        verificationQueue.verify();

        return null;
    }
}
