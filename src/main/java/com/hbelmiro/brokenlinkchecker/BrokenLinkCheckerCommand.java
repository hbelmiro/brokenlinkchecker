package com.hbelmiro.brokenlinkchecker;

import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Flow;

@Command(name = "broken-link-checker", mixinStandardHelpOptions = true)
public class BrokenLinkCheckerCommand implements Runnable {

    @CommandLine.Option(names = {"-rc", "--root-context"}, required = true)
    private String rootContext;

    @CommandLine.Option(names = {"-p", "--page"}, required = true)
    private String page;

    private final BrokenLinkChecker brokenLinkChecker;

    @Inject
    BrokenLinkCheckerCommand(BrokenLinkChecker brokenLinkChecker) {
        this.brokenLinkChecker = brokenLinkChecker;
    }

    @Override
    public void run() {
        brokenLinkChecker.check(rootContext, page)
                .subscribe(new Flow.Subscriber<>() {
                    @Override
                    public void onSubscribe(Flow.Subscription subscription) {
                        subscription.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(String item) {
                        System.out.println(item);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        System.exit(1);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("COMPLETE!!!");
                    }
                });
    }

}
