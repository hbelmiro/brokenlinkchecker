package com.hbelmiro.brokenlinkchecker;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

class VerificationQueue {

    private final Map<String, Verification> verifications = new HashMap<>();

    private final String rootContext;

    private final String page;

    VerificationQueue(String rootContext, String page) {
        this.rootContext = rootContext;
        this.page = page;
        verifications.put(page, new Verification());
    }

    void add(String url) {
        if (!verifications.containsKey(url)) {
            verifications.put(url, new Verification());
        }
    }

    public Flow.Publisher<String> verify() {
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        new Thread(() -> {
            do {
                Map.Entry<String, Verification> verification = verifications.entrySet().stream()
                        .filter(entry -> !entry.getValue().isVerified())
                        .findFirst()
                        .orElseThrow();

                Document document;
                try {
                    document = Jsoup.connect(verification.getKey()).get();
                    publisher.submit(verification.getKey() + ": OK");
                } catch (HttpStatusException e) {
                    publisher.submit(verification.getKey() + ": " + e.getStatusCode());
                    continue;
                } catch (IllegalArgumentException | IOException e) {
                    publisher.submit(verification.getKey() + ": Unexpected Exception: " + e.getMessage());
                    continue;
                } finally {
                    verification.getValue().setVerified(true);
                }

                if (verification.getKey().startsWith(page)) {
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

            publisher.close();
        }).start();

        return publisher;
    }

    private boolean isQueueEmpty() {
        return verifications.entrySet().stream()
                .allMatch(entry -> entry.getValue().isVerified());
    }
}
