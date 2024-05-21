package com.hbelmiro.brokenlinkchecker;

import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "broken-link-checker", mixinStandardHelpOptions = true)
public class BrokenLinkCheckerCommand implements Runnable {

    @CommandLine.Option(names = {"-rc", "--root-context"}, required = true)
    private String rootContext;

    @CommandLine.Option(names = {"-p", "--page"}, required = true)
    private String page;

    @CommandLine.Option(names = {"-e", "--show-only-errors"}, defaultValue = "false")
    private boolean showOnlyErrors;

    private final BrokenLinkChecker brokenLinkChecker;

    @Inject
    BrokenLinkCheckerCommand(BrokenLinkChecker brokenLinkChecker) {
        this.brokenLinkChecker = brokenLinkChecker;
    }

    @Override
    public void run() {
        VerificationOptions verificationOptions = VerificationOptions.newBuilder()
                .showOnlyErrors(showOnlyErrors)
                .build();

        brokenLinkChecker.check(
                rootContext,
                page,
                verificationOptions,
                System.out::println
        );


        System.out.println("✅ Verification complete!");
    }
}
