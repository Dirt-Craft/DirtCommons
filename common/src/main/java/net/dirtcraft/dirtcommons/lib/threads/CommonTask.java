package net.dirtcraft.dirtcommons.lib.threads;

import net.dirtcraft.dirtcommons.api.tasks.Delay;
import net.dirtcraft.dirtcommons.api.tasks.Task;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToLongFunction;

public class CommonTask<T, S> implements Task<T>, Comparable<CommonTask<?, ?>> {
    protected final AbstractTaskDispatcher.ThreadType thread;
    protected final ScheduleType type;
    protected final long executionTimestamp;
    protected final Function<T, S> execute;
    protected CommonTask<S, ?> next = null;
    protected volatile T arg;
    protected volatile S ret;
    public CommonTask(Function<T, S> execute, AbstractTaskDispatcher.ThreadType thread, AbstractTaskDispatcher dispatcher, long time, Delay unit) {
        this.thread = thread;
        this.execute = execute;
        this.type = time <= 0? ScheduleType.INSTANTLY: unit == Delay.TICKS? ScheduleType.TICK_TIME: ScheduleType.REAL_TIME;
        this.executionTimestamp = dispatcher.getCurrentTime() + unit.toMs(time);
    }

    public boolean canExecute(AbstractTaskDispatcher dispatcher) {
        return type.canExecute(this, dispatcher);
    }

    public void execute(AbstractTaskDispatcher dispatcher) {
        thread.getExecutor(dispatcher).execute(()->{
            try {
                ret = execute.apply(arg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (next != null) {
                next.arg = ret;
            }
        });
    }

    @Override
    public int compareTo(CommonTask<?, ?> o) {
        return Long.compare(this.executionTimestamp, o.executionTimestamp);
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof CommonTask)) return false;
        CommonTask<?, ?> task = (CommonTask<?, ?>) o;
        return task.executionTimestamp == this.executionTimestamp &&
                task.type == this.type &&
                task.execute == this.execute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, executionTimestamp, execute);
    }

    public abstract static class Builder<T, S> implements Task.Builder<S> {
        protected CommonTask<?, ?> root = null;
        protected CommonTask<?, T> parent = null;
        protected AbstractTaskDispatcher.ThreadType thread;
        protected AbstractTaskDispatcher dispatcher;
        protected long delay;
        protected Delay unit;
        protected Function<T, S> execute;

        public <U> Builder<S, U> thenApply(Function<S, U> run, boolean async, long delay, Delay unit) {
            CommonTask<T, S> task = new CommonTask<>(this.execute, this.thread, this.dispatcher, this.delay, this.unit);
            if (root == null) root = task;
            else parent.next = task;
            Builder<S, U> b = (Builder<S, U>)this;
            b.thread = async? AbstractTaskDispatcher.ThreadType.ASYNC: AbstractTaskDispatcher.ThreadType.SERVER;
            b.delay = delay;
            b.unit = unit;
            b.execute = run;
            b.parent = task;
            return b;
        }
    }

    protected enum ScheduleType {
        TICK_TIME(AbstractTaskDispatcher::getCurrentTick),
        REAL_TIME(AbstractTaskDispatcher::getCurrentTime),
        INSTANTLY(x->0L){@Override public boolean canExecute(CommonTask<?, ?> task, AbstractTaskDispatcher dispatcher){return true;}};
        private final ToLongFunction<AbstractTaskDispatcher> func;
        ScheduleType(ToLongFunction<AbstractTaskDispatcher> getter){
            this.func = getter;
        }

        public boolean canExecute(CommonTask<?, ?> task, AbstractTaskDispatcher dispatcher){
            return func.applyAsLong(dispatcher) >= task.executionTimestamp;
        }
    }
}
