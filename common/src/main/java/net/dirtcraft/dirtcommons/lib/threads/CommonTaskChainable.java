package net.dirtcraft.dirtcommons.lib.threads;

import net.dirtcraft.dirtcommons.api.tasks.Delay;
import net.dirtcraft.dirtcommons.api.tasks.Scheduler;
import net.dirtcraft.dirtcommons.api.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CommonTaskChainable<T, R> extends CommonTask<T, R> implements Task.TaskChainable<R> {
    protected final List<CommonTaskChainable<R, ?>> next;

    public boolean repeating() {
        return false;
    }

    public CommonTaskChainable(boolean async, long time, Delay unit) {
        super(async, time, unit);
        this.next = new ArrayList<>();
    }

    @Override
    protected void run(AbstractTaskScheduler dispatcher) {
        this.worker = Thread.currentThread();
        try {
            result = execute();
            setState(State.EXECUTING, State.FINISHED_SUCCESS);
        } catch (Exception e) {
            setState(State.EXECUTING, State.FINISHED_FAILURE);
            e.printStackTrace();
        } finally {
            notifyAll();
        }
        next.forEach(t->{
            t.supply(result);
            dispatcher.register(next);
        });
    }

    @Override
    public Chain<R> chain() {
        return new Builder<>(this);
    }

    protected abstract void supply(T t);

    public static class FunctionStage<T, U> extends CommonTaskChainable<T, U> {
        private final Function<T, U> function;
        private volatile T arg;

        public FunctionStage(Function<T, U> function, boolean async, long time, Delay unit) {
            super(async, time, unit);
            this.function = function;
        }

        @Override
        protected void supply(T t) {
            this.arg = t;
        }

        @Override
        protected U execute() {
            return function.apply(arg);
        }
    }

    public static class ConsumerStage<T> extends CommonTaskChainable<T, Void> {
        private final Consumer<T> consumer;
        private volatile T arg;

        public ConsumerStage(Consumer<T> consumer, boolean async, long time, Delay unit) {
            super(async, time, unit);
            this.consumer = consumer;
        }

        @Override
        protected void supply(T t) {
            this.arg = t;
        }

        @Override
        protected Void execute() {
            consumer.accept(arg);
            return null;
        }
    }

    public static class SupplierStage<T, U> extends CommonTaskChainable<T, U> {
        private final Supplier<U> supplier;

        public SupplierStage(Supplier<U> supplier, boolean async, long time, Delay unit) {
            super(async, time, unit);
            this.supplier = supplier;
        }

        @Override
        protected void supply(T unused) { }

        @Override
        protected U execute() {
            return supplier.get();
        }
    }

    public static class RunnableStage<T> extends CommonTaskChainable<T, Void> {
        private final Runnable runnable;

        public RunnableStage(Runnable runnable, boolean async, long time, Delay unit) {
            super(async, time, unit);
            this.runnable = runnable;
        }

        @Override
        protected void supply(T unused) { }

        @Override
        protected Void execute() {
            runnable.run();
            return null;
        }
    }

    public static class Builder<T> implements Chain<T> {
        protected final CommonTaskChainable<?, ?> root;
        protected CommonTaskChainable<?, T> current;

        private Builder(CommonTaskChainable<?, T> root) {
            this.root = this.current = root;
        }

        @Override
        public <U> Builder<U> thenApply(Function<T, U> run, boolean async, long delay, Delay unit) {
            return addTask(new FunctionStage<>(run, async, delay, unit));
        }

        @Override
        public <U> Builder<U> thenGet(Supplier<U> run, boolean async, long delay, Delay unit) {
            return addTask(new SupplierStage<>(run, async, delay, unit));
        }

        @Override
        public Builder<Void> thenAccept(Consumer<T> run, boolean async, long delay, Delay unit) {
            return addTask(new ConsumerStage<>(run, async, delay, unit));
        }

        @Override
        public Builder<Void> thenRun(Runnable run, boolean async, long delay, Delay unit) {
            return addTask(new RunnableStage<>(run, async, delay, unit));
        }

        @Override
        public Builder<T> shouldRunAsync(boolean async) {
            current.async = async;
            return this;
        }

        @Override
        public Builder<T> delay(long time, Delay unit) {
            current.delayUnit = unit;
            current.delayLength = time;
            return this;
        }

        @Override
        public Task<T> submit(Scheduler scheduler) {
            scheduler.register(root);
            return current;
        }

        @Override
        public Task<T> current(){
            return current;
        }

        @Override
        public Task<?> root(){
            return root;
        }

        protected <U> Builder<U> addTask(CommonTaskChainable<T, U> task) {
            current.next.add(task);
            Builder<U> b = (Builder<U>)this;
            b.current = task;
            return b;
        }
    }
}
