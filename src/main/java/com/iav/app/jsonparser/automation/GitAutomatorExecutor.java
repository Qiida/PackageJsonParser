package com.iav.app.jsonparser.automation;

import com.iav.git.GitAutomator;
import com.iav.git.Update;
import com.iav.utils.LoggerUtils;

import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GitAutomatorExecutor implements Runnable {
    final public static ConcurrentLinkedDeque<Update> UPDATES = new ConcurrentLinkedDeque<com.iav.git.Update>();
    final private GitAutomator gitAutomator;

    final private int duration;

    final private TimeUnit timeUnit;
    private static  final Logger LOGGER = LoggerUtils.getLOGGER();

    public GitAutomatorExecutor(String path, int duration, TimeUnit timeUnit) {
        gitAutomator = new GitAutomator(path);
        this.duration = duration;
        this.timeUnit = timeUnit;
    }
    @Override
    public void run() {

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);
        executorService.scheduleAtFixedRate(() -> {
            Update update = gitAutomator.run();
//            LOGGER.info(Thread.currentThread().getName()+": Number of Changes: "+update.getChanges().size());
            if (update.isAlreadyUpToDate) {
                LOGGER.info(Thread.currentThread().getName()+": GitAutomator("+gitAutomator.getPath()+") -> already up to date");
            } else {
                if (!update.getChanges().isEmpty()) {
                    Stack<String> changes = new Stack<>();
                    for (String change : update.getChanges()) {
                        changes.push(change);
                    }
                    StringBuilder changesStringBuilder = new StringBuilder();
                    while(!changes.isEmpty()) {
                        changesStringBuilder.append(LoggerUtils.LEFT_PAD).append("  ").append(changes.pop()).append("\n");
                    }
                    LOGGER.info(
                            Thread.currentThread().getName()+": GitAutomator("+update.getPath()+") -> new update"+"\n"+
                                    LoggerUtils.LEFT_PAD+"From: "+update.getPre().getSHA()+" "+update.getPre().getDate()+"\n"+
                                    LoggerUtils.LEFT_PAD+"  To: "+update.getPost().getSHA()+" "+update.getPost().getDate()+"\n\n"+
                                    changesStringBuilder

                    );
                    UPDATES.push(update);
                }
            }
        }, 0, duration, timeUnit);
    }
}
