package net.dirtcraft.dirtcommons.threads;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface ThreadManager {
    Executor getSyncExecutor();
    ScheduledExecutorService getAsyncExecutor();

    void scheduleTickDelayedTask(int ticks, Runnable runnable);
    ScheduledFuture<?> scheduleAsyncTask(long time, TimeUnit unit, Runnable runnable);
    void runAsyncTask(Runnable runnable);
    void runSyncTask(Runnable runnable);
}
