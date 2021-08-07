package net.dirtcraft.dirtcommons.api.tasks;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Task<T> extends Future<T> {



    interface Builder<S> {
        <U> Builder<U> thenApply(Function<S, U> run, boolean async, long delay, Delay unit);

        default Builder<Void> thenAccept(Consumer<S> run, boolean async, long delay, Delay unit) {
            return thenApply(s->{
                run.accept(s);
                return null;
            }, async, delay, unit);
        }

        default Builder<Void> thenRun(Runnable run, boolean async, long delay, Delay unit) {
            return thenApply(a->{
                run.run();
                return null;
            }, async, delay, unit);
        }

        default  <U> Builder<U> thenGet(Supplier<U> run, boolean async, long delay, Delay unit) {
            return thenApply(a->run.get(), async, delay, unit);
        }

        default <U> Builder<U> thenApply(Function<S, U> run) {
            return thenApply(run, false, 0L, null);
        }

        default <U> Builder<U> thenApplyAsync(Function<S, U> run) {
            return thenApply(run, true, 0L, null);
        }

        default <U> Builder<U> thenApply(Function<S, U> run, long delay, Delay unit) {
            return thenApply(run, false, delay, unit);
        }

        default <U> Builder<U> thenApplyAsync(Function<S, U> run, long delay, Delay unit) {
            return thenApply(run, true, delay, unit);
        }

        default Builder<Void> thenAccept(Consumer<S> run) {
            return thenAccept(run, false, 0L, null);
        }

        default Builder<Void> thenAcceptAsync(Consumer<S> run) {
            return thenAccept(run, true, 0L, null);
        }

        default Builder<Void> thenAccept(Consumer<S> run, long delay, Delay unit) {
            return thenAccept(run, false, delay, unit);
        }

        default Builder<Void> thenAcceptAsync(Consumer<S> run, long delay, Delay unit) {
            return thenAccept(run, true, delay, unit);
        }

        default Builder<Void> thenRun(Runnable run) {
            return thenRun(run, false, 0L, null);
        }

        default Builder<Void> thenRunAsync(Runnable run) {
            return thenRun(run, true, 0L, null);
        }

        default Builder<Void> thenRun(Runnable run, long delay, Delay unit) {
            return thenRun(run, false, delay, unit);
        }

        default Builder<Void> thenRunAsync(Runnable run, long delay, Delay unit) {
            return thenRun(run, true, delay, unit);
        }

        default  <U> Builder<U> thenGet(Supplier<U> run) {
            return thenGet(run, false, 0L, null);
        }

        default  <U> Builder<U> thenGetAsync(Supplier<U> run) {
            return thenGet(run, true, 0L, null);
        }

        default  <U> Builder<U> thenGet(Supplier<U> run, long delay, Delay unit) {
            return thenGet(run, false, delay, unit);
        }

        default  <U> Builder<U> thenGetAsync(Supplier<U> run, long delay, Delay unit) {
            return thenGet(run, true, delay, unit);
        }
    }
}
