package com.hbelmiro.brokenlinkchecker;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

class VerificationQueue {

    private Map<String, Verification> verifications = new HashMap<>();

    private final String rootContext;

    VerificationQueue(String rootContext) {
        this.rootContext = rootContext;
        verifications.put(rootContext, new Verification(rootContext));
    }

    void add(String url) {
        if (!verifications.containsKey(url)) {
            verifications.put(url, new Verification(url));
        }
    }

    public void verify() {
        do {
            Map.Entry<String, Verification> verification = verifications.entrySet().stream()
                    .filter(entry -> !entry.getValue().isVerified())
                    .findFirst()
                    .orElseThrow();

            Document document;
            try {
                document = Jsoup.connect(verification.getKey()).get();
                System.out.println(verification.getKey() + ": OK");
            } catch (HttpStatusException e) {
                verification.getValue().setStatusCode(e.getStatusCode());
                System.out.println(verification.getKey() + ": " + e.getStatusCode());
                continue;
            } catch (IllegalArgumentException | IOException e) {
                System.out.println(verification.getKey() + ": Unexpected Exception: " + e.getMessage());
                continue;
            } finally {
                verification.getValue().setVerified(true);
            }

            if (verification.getKey().startsWith(rootContext)) {
                Elements links = document.select("a[href]");

                for (Element link : links) {
                    String linkHref = link.attr("href");
                    if (linkHref.startsWith("/")) {
                        linkHref = rootContext + linkHref;
                    }
                    if (linkHref.endsWith("/")) {
                        linkHref = linkHref.substring(0, linkHref.length() - 1);
                    }
                    add(linkHref);
                }
            }
        } while (!isQueueEmpty());
    }

    private boolean isQueueEmpty() {
        return verifications.entrySet().stream()
                .allMatch(entry -> entry.getValue().isVerified());
    }
}
