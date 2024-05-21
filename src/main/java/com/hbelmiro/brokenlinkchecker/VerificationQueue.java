package com.hbelmiro.brokenlinkchecker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class VerificationQueue {

    private static final String OK = "✅";

    private static final String NOT_OK = "⛔";

    private final List<String> toVerify = new ArrayList<>();

    private final Map<String, Verification> verifiedLinks = new HashMap<>();

    private final String rootContext;

    private final String page;

    private final Consumer<String> onCheckStatus;

    VerificationQueue(String rootContext, String page, Consumer<String> onCheckStatus) {
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
            return List.of();
        }

        Elements links = document.select("a[href]");

        List<String> linksToReturn = new ArrayList<>();

        for (Element link : links) {
            String linkHref = link.attr("href");
            if (linkHref.startsWith("/")) {
                linkHref = rootContext + linkHref;
            } else if (linkHref.startsWith("#")) {
                linkHref = url + linkHref;
            }
            if (linkHref.endsWith("/")) {
                linkHref = linkHref.substring(0, linkHref.length() - 1);
            }
            linksToReturn.add(linkHref);
        }

        return linksToReturn;
    }

    private String checkUrl(String url) {
        Verification verification = verifiedLinks.get(url);
        if (verification != null) {
            return verification.getStatus();
        } else {
            try {
                Jsoup.connect(url).get();
                verification = new Verification();
                verification.setStatus(OK);
                verifiedLinks.put(url, verification);
                return OK;
            } catch (Exception e) {
                verification = new Verification();
                String status = NOT_OK;
                verification.setStatus(status);
                verifiedLinks.put(url, verification);
                return status;
            }
        }
    }

    public void verify(VerificationOptions verificationOptions) {
        while (!toVerify.isEmpty()) {
            String current = toVerify.get(0);
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
                if (OK.equals(status) && link.startsWith(page) && !link.contains("#")) {
                    toVerify.add(link);
                }
            });
        }
    }
}
