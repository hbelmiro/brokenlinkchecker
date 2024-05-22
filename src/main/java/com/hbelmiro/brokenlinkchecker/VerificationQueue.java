package com.hbelmiro.brokenlinkchecker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

class VerificationQueue {

    private static final String OK = "✅";

    private static final String NOT_OK = "⛔";

    private final Set<String> toVerify = new HashSet<>();

    private final Map<String, Verification> verifiedLinks = new HashMap<>();

    private final String rootContext;

    private final String page;

    private final Consumer<String> onCheckStatus;

    private final List<String> ignoredUrls;

    VerificationQueue(List<String> ignoredUrls, String rootContext, String page, Consumer<String> onCheckStatus) {
        this.ignoredUrls = ignoredUrls;
        this.rootContext = rootContext;
        this.page = page;
        this.onCheckStatus = onCheckStatus;
        toVerify.add(page);
    }

    private List<String> getLinks(String url) {
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (Exception e) {
            Verification verification = new Verification();
            verification.setStatus(NOT_OK);
            verifiedLinks.put(url, verification);
            return List.of();
        }

        Elements links = document.select("a[href]");

        List<String> linksToReturn = new ArrayList<>();

        for (Element link : links) {
            String linkHref = link.attr("href");
            if (linkHref.startsWith("//")) {
                linkHref = "http:" + linkHref;
            } else if (linkHref.startsWith("/")) {
                linkHref = rootContext + linkHref;
            } else if (linkHref.startsWith("#")) {
                linkHref = url + linkHref;
            }
            if (linkHref.endsWith("/")) {
                linkHref = linkHref.substring(0, linkHref.length() - 1);
            }

            if (!isIgnored(linkHref)) {
                linksToReturn.add(linkHref);
            }
        }

        return linksToReturn;
    }

    private boolean isIgnored(String linkHref) {
        return ignoredUrls.stream().anyMatch(linkHref::matches);
    }

    private String checkUrl(String url) {
        Verification verification = verifiedLinks.get(url);
        if (verification != null) {
            return verification.getStatus();
        } else {
            try {
                Jsoup.connect(url).get();
                return OK;
            } catch (Exception e) {
                return NOT_OK;
            }
        }
    }

    public void verify(VerificationOptions verificationOptions) {
        while (!toVerify.isEmpty()) {
            String current = toVerify.stream().findAny().orElseThrow();
            toVerify.remove(current);

            onCheckStatus.accept("Getting links from: " + current);
            List<String> links = getLinks(current);

            links.forEach(link -> {
                String status = checkUrl(link);
                if (verificationOptions.isShowOnlyErrors()) {
                    if (NOT_OK.equals(status)) {
                        onCheckStatus.accept("├───" + status + "─── " + link);
                    }
                } else {
                    onCheckStatus.accept("├───" + status + "─── " + link);
                }
                if (!verifiedLinks.containsKey(link) && OK.equals(status) && link.startsWith(page) && !link.contains("#")) {
                    toVerify.add(link);
                }

                var verification = new Verification();
                verification.setStatus(OK);
                verifiedLinks.put(current, verification);
            });
        }
    }
}
