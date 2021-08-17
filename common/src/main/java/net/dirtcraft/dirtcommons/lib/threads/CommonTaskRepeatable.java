package net.dirtcraft.dirtcommons.lib.threads;

import net.dirtcraft.dirtcommons.api.tasks.Delay;

public class CommonTaskRepeatable extends CommonTask<Void, Void> {
    private final Runnable runnable;

    public CommonTaskRepeatable(Runnable run, boolean async, long time, Delay unit) {
        super(async, time, unit);
        this.runnable = run;
    }

    @Override
    protected Void execute() {
        runnable.run();
        this.state = State.WAITING;
        return null;
    }

    protected void run(AbstractTaskScheduler dispatcher) {
        this.worker = Thread.currentThread();
        try {
            result = execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean repeating() {
        return true;
    }
}
