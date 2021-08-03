package net.dirtcraft.dirtcommons.api.tasks;


import net.dirtcraft.dirtcommons.api.function.LongToLongFunction;

import java.util.concurrent.TimeUnit;

public enum Delay {
    MILLISECONDS(TimeUnit.MILLISECONDS::toMillis),
    SECONDS(TimeUnit.SECONDS::toMillis),
    MINUTES(TimeUnit.MINUTES::toMillis),
    HOURS(TimeUnit.HOURS::toMillis),
    DAYS(TimeUnit.DAYS::toMillis),
    TICKS(t->t);
    private final LongToLongFunction func;
    Delay(LongToLongFunction func){
        this.func = func;
    }

    public long toMs(long in){
        return func.apply(in);
    }
}
