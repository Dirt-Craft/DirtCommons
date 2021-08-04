package net.dirtcraft.dirtcommons.lib.threads;

import net.dirtcraft.dirtcommons.api.tasks.Delay;
import net.dirtcraft.dirtcommons.api.tasks.Task;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToLongFunction;

public class CommonTask<T, U> implements Task<U> {
    protected final boolean async;
    protected final ScheduleType type;
    protected final long executionTimestamp;
    protected final Function<T, U> execute;
    protected CommonTask<U, ?> next = null;
    protected volatile T arg;
    protected volatile U ret;

    public CommonTask(Function<T, U> execute, boolean async, long time, Delay unit) {
        this.async = async;
        this.execute = execute;
        this.type = time <= 0? ScheduleType.INSTANTLY: unit == Delay.TICKS? ScheduleType.TICK_TIME: ScheduleType.REAL_TIME;
        this.executionTimestamp = System.currentTimeMillis() + unit.toMs(time);
    }

    public boolean canExecute(AbstractTaskScheduler dispatcher) {
        return type.canExecute(this, dispatcher);
    }

    public void execute(AbstractTaskScheduler dispatcher) {
        dispatcher.getExecutor(async).execute(()->{
            try {
                ret = execute.apply(arg);
                dispatcher.register(next);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (next != null) {
                next.arg = ret;
            }
        });
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
            current.next = task;
            Builder<U> b = (Builder<U>)this;
            b.current = task;
            return b;
        }
    }

    protected enum ScheduleType {
        TICK_TIME(AbstractTaskScheduler::getCurrentTick),
        REAL_TIME(AbstractTaskScheduler::getCurrentTime),
        INSTANTLY(x->0L){@Override public boolean canExecute(CommonTask<?, ?> task, AbstractTaskScheduler dispatcher){return true;}};
        private final ToLongFunction<AbstractTaskScheduler> func;
        ScheduleType(ToLongFunction<AbstractTaskScheduler> getter){
            this.func = getter;
        }

        public boolean canExecute(CommonTask<?, ?> task, AbstractTaskScheduler dispatcher){
            return func.applyAsLong(dispatcher) >= task.executionTimestamp;
        }
    }
}
