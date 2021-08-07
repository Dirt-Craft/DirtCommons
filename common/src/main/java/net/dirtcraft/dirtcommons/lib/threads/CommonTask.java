package net.dirtcraft.dirtcommons.lib.threads;

import net.dirtcraft.dirtcommons.api.tasks.Delay;
import net.dirtcraft.dirtcommons.api.tasks.Task;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class CommonTask<T, U> implements Task<U> {
    protected final List<CommonTask<U, ?>> next;
    protected final Function<T, U> execute;
    protected final boolean async;
    protected final Delay delayUnit;
    protected final long delayLength;
    protected long delayUntil;
    protected volatile Thread worker;
    protected volatile State state;
    protected volatile Exception err;
    protected volatile U result;
    protected volatile T arg;

    public CommonTask(Function<T, U> execute, boolean async, long time, Delay unit) {
        this.async = async;
        this.execute = execute;
        this.delayLength = time;
        this.delayUnit = unit;
        this.next = new ArrayList<>();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (mayInterruptIfRunning && state == State.EXECUTING) worker.interrupt();
        return setState(State.WAITING, State.CANCELLED) == State.CANCELLED;
    }

    @Override
    public boolean isCancelled() {
        return state == State.CANCELLED;
    }

    @Override
    public boolean isDone() {
        return state.finished;
    }

    @Override
    public U get() throws InterruptedException, ExecutionException {
        if (err != null) throw new ExecutionException(err);
        if (state.finished) return result;
        wait();
        return get();
    }

    @Override
    public U get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (err != null) throw new ExecutionException(err);
        if (state.finished) return result;
        wait(unit.toMillis(timeout));
        if (!state.finished) throw new TimeoutException();
        return get();
    }

    public boolean canExecute(AbstractTaskScheduler dispatcher) {
        if (setState(State.REGISTERING, State.WAITING) == State.REGISTERING) {
            if (delayUnit == Delay.TICKS) delayUntil = dispatcher.getCurrentGameTick() + delayLength;
        } else delayUntil = delayUnit.toMs(delayLength);
        if (delayUnit == Delay.TICKS) return delayUntil <= dispatcher.getCurrentGameTick();
        else return delayUntil >= System.currentTimeMillis();
    }

    public void execute(AbstractTaskScheduler dispatcher) {
        if (setState(State.WAITING, State.EXECUTING) != State.EXECUTING) {
            notifyAll();
            return;
        }
        dispatcher.getExecutor(async).execute(()->{
            if (state != State.EXECUTING) {
                notifyAll();
                return;
            }
            else this.worker = Thread.currentThread();
            try {
                result = execute.apply(arg);
                setState(State.EXECUTING, State.FINISHED_SUCCESS);
                dispatcher.register(next);
            } catch (Exception e) {
                setState(State.EXECUTING, State.FINISHED_FAILURE);
                e.printStackTrace();
            } finally {
                notifyAll();
            }
            if (next != null) {
                next.forEach(next->next.arg = result);
            }
        });
    }

    private synchronized State setState(State expected, State updated){
        if (state == expected) state = updated;
        return state;
    }

    public abstract static class Builder<T> implements Task.Builder<T> {
        protected final CommonTask<?, ?> root;
        protected CommonTask<?, T> current;

        public Builder(Supplier<T> run, boolean async, long delay, Delay unit) {
            CommonTask<?, T> task = new CommonTask<>(a->run.get(), async, delay, unit);
            root = current = task;
        }

        public <U> Builder<U> thenApply(Function<T, U> run, boolean async, long delay, Delay unit) {
            CommonTask<T, U> task = new CommonTask<>(run, async, delay, unit);
            current.next.add(task);
            Builder<U> b = (Builder<U>)this;
            b.current = task;
            return b;
        }
    }

    private enum State{
        REGISTERING(false),
        WAITING(false),
        EXECUTING(false),
        FINISHED_SUCCESS(true),
        FINISHED_FAILURE(true),
        CANCELLED(true);
        private final boolean finished;
        State(boolean b){
            this.finished = b;
        }
    }
}
