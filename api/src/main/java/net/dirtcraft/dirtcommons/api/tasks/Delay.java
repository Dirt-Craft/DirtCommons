package net.dirtcraft.dirtcommons.api.tasks;


import net.dirtcraft.dirtcommons.api.function.LongToLongFunction;

import java.util.concurrent.TimeUnit;

public enum Delay {
    /**
     * A collection of units to be used to convert to milliseconds.
     * Based off of TimeUnit, with the addition of tick which returns itself.
     * Notably, ticks are based off in-game time server tick rate,
     * not real time unlike the others.
     */
    MILLISECONDS(TimeUnit.MILLISECONDS::toMillis),
    SECONDS(TimeUnit.SECONDS::toMillis),
    MINUTES(TimeUnit.MINUTES::toMillis),
    HOURS(TimeUnit.HOURS::toMillis),
    DAYS(TimeUnit.DAYS::toMillis),
    TICKS(t->t /*"* 50"*/);
    private final LongToLongFunction func;
    Delay(LongToLongFunction func){
        this.func = func;
    }

    public long toMs(long in){
        return func.apply(in);
    }
}
