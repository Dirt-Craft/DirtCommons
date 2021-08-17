package net.dirtcraft.dirtcommons.lib.threads;

import net.dirtcraft.dirtcommons.api.tasks.Delay;
import net.dirtcraft.dirtcommons.api.tasks.Task;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public abstract class CommonTask<T, R> implements Task<R> {
    protected boolean async;
    protected Delay delayUnit;
    protected long delayLength;

    protected volatile long executeAfter;
    protected volatile Thread worker;
    protected volatile State state = State.REGISTERING;
    protected volatile Exception err;
    protected volatile R result;

    protected abstract R execute();

    protected abstract boolean repeating();

    public CommonTask(boolean async, long time, Delay unit) {
        this.async = async;
        this.delayLength = time;
        this.delayUnit = unit;
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
    public R get() throws InterruptedException, ExecutionException {
        if (err != null) throw new ExecutionException(err);
        if (state.finished) return result;
        wait();
        return get();
    }

    @Override
    public R get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (err != null) throw new ExecutionException(err);
        if (state.finished) return result;
        wait(unit.toMillis(timeout));
        if (!state.finished) throw new TimeoutException();
        return get();
    }

    public boolean canExecute(AbstractTaskScheduler dispatcher) {
        if (delayUnit == Delay.TICKS) return executeAfter <= dispatcher.getCurrentGameTick();
        else return executeAfter >= System.currentTimeMillis();
    }

    protected boolean calculateDelay(AbstractTaskScheduler dispatcher) {
        if (setState(State.REGISTERING, State.WAITING) != State.WAITING) return false;
        if (delayUnit == Delay.TICKS) executeAfter = dispatcher.getCurrentGameTick() + delayLength;
        else executeAfter = delayUnit.toMs(delayLength);
        return true;
    }

    public void execute(AbstractTaskScheduler dispatcher) {
        if (setState(State.WAITING, State.EXECUTING) != State.EXECUTING) notifyAll();
        else dispatcher.getExecutor(async).execute(()-> run(dispatcher));
    }

    protected abstract void run(AbstractTaskScheduler dispatcher);

    synchronized State setState(State expected, State updated){
        if (state == expected) state = updated;
        return state;
    }

    enum State{
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
