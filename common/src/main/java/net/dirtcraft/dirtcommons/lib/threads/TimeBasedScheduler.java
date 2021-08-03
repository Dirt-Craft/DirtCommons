package net.dirtcraft.dirtcommons.lib.threads;

import java.util.concurrent.TimeUnit;

public class TimeBasedScheduler {
    public void a(TimeUnit tu, long dura) {
        long t = System.currentTimeMillis() + tu.toMillis(dura);
    }
}