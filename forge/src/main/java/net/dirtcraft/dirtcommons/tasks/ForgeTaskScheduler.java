package net.dirtcraft.dirtcommons.tasks;

import net.dirtcraft.dirtcommons.lib.threads.AbstractTaskScheduler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

public class ForgeTaskScheduler extends AbstractTaskScheduler {
    private volatile long currentTick = 0L;
    public ForgeTaskScheduler(MinecraftServer server) {
        super(server);
        MinecraftForge.EVENT_BUS.addListener(this::tick);
    }

    @Override
    protected long getCurrentGameTick() {
        return currentTick;
    }

    //Setting is atomic, and we are only setting here on the server thread so it's fine.
    @SuppressWarnings("NonAtomicOperationOnVolatileField")
    private void tick(TickEvent.ServerTickEvent event) {
        currentTick++;
    }
}
