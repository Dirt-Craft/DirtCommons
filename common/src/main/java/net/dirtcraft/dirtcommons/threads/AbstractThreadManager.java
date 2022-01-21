package net.dirtcraft.dirtcommons.threads;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractThreadManager implements ThreadManager{
    private final ThreadGroup threadGroup = new ThreadGroup("DirtCommons Executor");
    private final AtomicInteger threadCount = new AtomicInteger();
    protected final ScheduledExecutorService async;

    public AbstractThreadManager(){
        this.async = Executors.newScheduledThreadPool(4, runnable->new Thread(threadGroup,
                String.format("DirtCommon-ThreadPool Working #%d", threadCount.getAndIncrement())){
            @Override
            public void run(){
                runnable.run();
            }
        });
    }

    @Override
    public ScheduledExecutorService getAsyncExecutor(){
        return async;
    }
}
