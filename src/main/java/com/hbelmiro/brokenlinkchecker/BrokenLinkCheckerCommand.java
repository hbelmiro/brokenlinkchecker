package com.hbelmiro.brokenlinkchecker;

import jakarta.inject.Inject;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.List;

@Command(name = "broken-link-checker", mixinStandardHelpOptions = true)
public class BrokenLinkCheckerCommand implements Runnable {

    @CommandLine.Option(names = {"-rc", "--root-context"}, required = true)
    private String rootContext;

    private final BrokenLinkChecker brokenLinkChecker;

    @Inject
    public BrokenLinkCheckerCommand(BrokenLinkChecker brokenLinkChecker) {
        this.brokenLinkChecker = brokenLinkChecker;
    }

    @Override
    public void run() {
        List<String> outputs = brokenLinkChecker.check(rootContext);
    }

}
