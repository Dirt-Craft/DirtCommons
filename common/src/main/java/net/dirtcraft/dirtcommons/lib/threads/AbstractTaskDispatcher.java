package net.dirtcraft.dirtcommons.lib.threads;

import net.dirtcraft.dirtcommons.api.tasks.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractTaskDispatcher extends Thread {
    protected final Executor asyncPool = Executors.newFixedThreadPool(8, WorkerThread::new);
    protected final Executor minecraftServer;
    protected volatile ArrayBlockingQueue<CommonTask<?, ?>> inbound = new ArrayBlockingQueue<>(99, true);
    protected List<CommonTask<?, ?>> tasks = new ArrayList<>();
    protected long gameTick = 0L;

    public AbstractTaskDispatcher(Executor minecraftServer) {
        this.minecraftServer = minecraftServer;
        setName("DirtCommons-Scheduler");
        setDaemon(true);
    }

    private void register(Task<?> task){
        this.inbound.offer((CommonTask<?, ?>) task);
    }

    long getCurrentTick() {
        return gameTick;
    }

    long getCurrentTime() {
        return System.currentTimeMillis();
    }

    protected abstract long getCurrentGameTick();

    @Override
    public void run(){
        try {
            sleep(25);
            this.gameTick = getCurrentGameTick();
            while (!inbound.isEmpty()) tasks.add(inbound.poll());
            Iterator<CommonTask<?, ?>> iter = tasks.iterator();
            while (iter.hasNext()){
                CommonTask<?, ?> task = iter.next();
                if (!task.canExecute(this)) continue;
                task.execute(this);
                if (task.next == task) continue;
                iter.remove();
                inbound.offer(task.next);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public enum ThreadType {
        SERVER(){
            @Override
            public Executor getExecutor(AbstractTaskDispatcher dispatcher) {
                return dispatcher.minecraftServer;
            }
        },
        ASYNC(){
            @Override
            public Executor getExecutor(AbstractTaskDispatcher dispatcher) {
                return dispatcher.asyncPool;
            }
        };

        public abstract Executor getExecutor(AbstractTaskDispatcher dispatcher);
    }

    private static final AtomicInteger count = new AtomicInteger(1);
    private static class WorkerThread extends Thread {
        private WorkerThread(Runnable runnable){
            super(runnable);
            setName(String.format("DirtCommons-Worker#%d", count.incrementAndGet()));
            setDaemon(true);
        }
    }



}
