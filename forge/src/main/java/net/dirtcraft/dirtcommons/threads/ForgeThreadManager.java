package net.dirtcraft.dirtcommons.threads;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ForgeThreadManager extends AbstractThreadManager{
    protected MinecraftServer server;
    public ForgeThreadManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void scheduleTickDelayedTask(int ticks, Runnable runnable) {
        server.tell(new TickDelayedTask(ticks, runnable));
    }

    public ScheduledFuture<?> scheduleAsyncTask(long time, TimeUnit unit, Runnable runnable){
        return async.schedule(runnable, time, unit);
    }

    public void runAsyncTask(Runnable runnable) {
        async.execute(runnable);
    }

    public void runSyncTask(Runnable runnable) {
        server.execute(runnable);
    }

    @Override
    public Executor getSyncExecutor() {
        return server;
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void setup(final FMLServerAboutToStartEvent event) {
        this.server = event.getServer();
    }

    private static class TickFutureTask extends TickDelayedTask{

        public TickFutureTask(int p_i50745_1_, Runnable p_i50745_2_) {
            super(p_i50745_1_, p_i50745_2_);
        }
    }

}
