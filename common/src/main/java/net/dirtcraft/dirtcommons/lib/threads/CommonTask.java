package net.dirtcraft.dirtcommons.lib.threads;

import net.dirtcraft.dirtcommons.api.tasks.Delay;
import net.dirtcraft.dirtcommons.api.tasks.Task;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;

public class CommonTask<T, U> implements Task<U>, Comparable<CommonTask<?, ?>> {
    protected final AbstractTaskDispatcher.ThreadType thread;
    protected final ScheduleType type;
    protected final long executionTimestamp;
    protected final Function<T, U> execute;
    protected CommonTask<U, ?> next = null;
    protected volatile T arg;
    protected volatile U ret;
    public CommonTask(Function<T, U> execute, AbstractTaskDispatcher.ThreadType thread, AbstractTaskDispatcher dispatcher, long time, Delay unit) {
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

    public abstract static class Builder<T> implements Task.Builder<T> {
        protected final AbstractTaskDispatcher dispatcher;
        protected final CommonTask<?, ?> root;
        protected CommonTask<?, T> current;

        protected Builder(AbstractTaskDispatcher dispatcher, Supplier<T> run, boolean async, long delay, Delay unit) {
            this.dispatcher = dispatcher;
            AbstractTaskDispatcher.ThreadType thread = async? AbstractTaskDispatcher.ThreadType.ASYNC: AbstractTaskDispatcher.ThreadType.SERVER;
            CommonTask<?, T> task = new CommonTask<>(a->run.get(), thread, dispatcher, delay, unit);
            root = current = task;
        }

        public <U> Builder<U> thenApply(Function<T, U> run, boolean async, long delay, Delay unit) {
            AbstractTaskDispatcher.ThreadType thread = AbstractTaskDispatcher.ThreadType.getType(async);
            CommonTask<T, U> task = new CommonTask<>(run, thread, dispatcher, delay, unit);
            current.next = task;
            Builder<U> b = (Builder<U>)this;
            b.current = task;
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
