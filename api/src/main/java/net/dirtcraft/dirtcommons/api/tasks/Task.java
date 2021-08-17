package net.dirtcraft.dirtcommons.api.tasks;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Task<T> extends Future<T> {



    interface TaskChainable<T> extends Task<T> {
        Chain<T> chain();
    }

    interface Chain<T> {
        /**
         * Assigns a function-task to execute after the current task is executed, then sets it as the current task in the chain.
         * @param run the function to be executed after the current task finishes.
         * @return the current chain instance, with the new task on the top of the chain.
         */
        default <U> Chain<U> thenApply(Function<T, U> run) {
            return thenApply(run, true, 0, Delay.NONE);
        }

        /**
         * Assigns a function-task to execute after the current task is executed, then sets it as the current task in the chain.
         * @param run the function to be executed after the current task finishes.
         * @param async should the thread be async
         * @param delay the duration of time required to execute this task. 0 or negative indicates instantly.
         * @param unit the unit of time. Note that all units except tick time runs based on IRL-time, tick is tps-based.
         * @return the current chain instance, with the new task on the top of the chain.
         */
        <U> Chain<U> thenApply(Function<T, U> run, boolean async, long delay, Delay unit);

        /**
         * Assigns a supplier-task to execute after the current task is executed, then sets it as the current task in the chain.
         * @param run the supplier to be executed after the current task finishes.
         * @return the current chain instance, with the new task on the top of the chain.
         */
        default <U> Chain<U> thenGet(Supplier<U> run) {
            return thenGet(run, true, 0, Delay.NONE);
        }

        /**
         * Assigns a supplier-task to execute after the current task is executed, then sets it as the current task in the chain.
         * @param run the supplier to be executed after the current task finishes.
         * @param async should the thread be async
         * @param delay the duration of time required to execute this task. 0 or negative indicates instantly.
         * @param unit the unit of time. Note that all units except tick time runs based on IRL-time, tick is tps-based.
         * @return the current chain instance, with the new task on the top of the chain.
         */
        <U> Chain<U> thenGet(Supplier<U> run, boolean async, long delay, Delay unit);

        /**
         * Assigns a consumer-task to execute after the current task is executed, then sets it as the current task in the chain.
         * @param run the consumer to be executed after the current task finishes.
         * @return the current chain instance, with the new task on the top of the chain.
         */
        default Chain<Void> thenAccept(Consumer<T> run) {
            return thenAccept(run, true, 0, Delay.NONE);
        }

        /**
         * Assigns a consumer-task to execute after the current task is executed, then sets it as the current task in the chain.
         * @param run the consumer to be executed after the current task finishes.
         * @param async should the thread be async
         * @param delay the duration of time required to execute this task. 0 or negative indicates instantly.
         * @param unit the unit of time. Note that all units except tick time runs based on IRL-time, tick is tps-based.
         * @return the current chain instance, with the new task on the top of the chain.
         */
        Chain<Void> thenAccept(Consumer<T> run, boolean async, long delay, Delay unit);

        /**
         * Assigns a runnable-task to execute after the current task is executed, then sets it as the current task in the chain.
         * @param run the runnable to be executed after the current task finishes.
         * @return the current chain instance, with the new task on the top of the chain.
         */
        default Chain<Void> thenRun(Runnable run) {
            return thenRun(run, true, 0, Delay.NONE);
        }

        /**
         * Assigns a runnable-task to execute after the current task is executed, then sets it as the current task in the chain.
         * @param run the runnable to be executed after the current task finishes.
         * @param async should the thread be async
         * @param delay the duration of time required to execute this task. 0 or negative indicates instantly.
         * @param unit the unit of time. Note that all units except tick time runs based on IRL-time, tick is tps-based.
         * @return the current chain instance, with the new task on the top of the chain.
         */
        Chain<Void> thenRun(Runnable run, boolean async, long delay, Delay unit);

        /**
         * sets if the task should be async or not. Note, the default state is asyc.
         * @param async should the thread be async
         * @return the current chain instance.
         */
        Chain<T> shouldRunAsync(boolean async);

        /**
         * Sets the task to execute on the main server tick loop.
         * @return the current chain instance.
         */
        default Chain<T> Sync() {
            return shouldRunAsync(false);
        }

        /**
         * Adds a delay to the task execution when it is registered. A task will be registered either if it has been
         * registered directly, or it is in a chain and the task before it has executed.
         * @param time the duration of time required to execute this task. 0 or negative indicates instantly.
         * @param unit the unit of time. Note that all units except tick time runs based on IRL-time, tick is tps-based.
         * @return the current chain instance.
         */
        Chain<T> delay(long time, Delay unit);

        /**
         * Submits this task chain for execution.
         * @param scheduler an instance of the task scheduler.
         * @return the final task to be executed.
         */
        Task<T> submit(Scheduler scheduler);

        /**
         * Simply grabs the current task being worked on.
         * @return the current task on the top of the chain.
         */
        Task<T> current();

        /**
         * Grabs the first task that needs to be executed.
         * @return the root task of the chain.
         */
        Task<?> root();

    }
}
