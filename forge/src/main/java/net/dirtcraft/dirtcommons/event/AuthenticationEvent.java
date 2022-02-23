package net.dirtcraft.dirtcommons.event;

import com.mojang.authlib.GameProfile;
import net.dirtcraft.dirtcommons.DirtCommons;
import net.dirtcraft.dirtcommons.threads.ThreadManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AuthenticationEvent extends Event {
    public final MinecraftServer server;
    public final GameProfile gameProfile;
    private ITextComponent cancelReason;
    private List<CompletableFuture<?>> tasks;
    private ThreadManager scheduler = DirtCommons.getInstance().getScheduler();

    public AuthenticationEvent(MinecraftServer server, GameProfile gameProfile) {
        this.server = server;
        this.gameProfile = gameProfile;
        this.tasks = new ArrayList<>();
    }

    public ITextComponent getCancelReason() {
        return cancelReason == null? new StringTextComponent("no reason specified"): cancelReason;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public void cancel(String reason) {
        super.setCanceled(true);
        this.cancelReason = new StringTextComponent(reason);
    }

    public void cancel(IFormattableTextComponent reason) {
        super.setCanceled(true);
        this.cancelReason = reason;
    }

    public CompletableFuture<?> addTask(Runnable runnable) {
        CompletableFuture<?> task = CompletableFuture.runAsync(runnable, scheduler.getAsyncExecutor());
        tasks.add(task);
        return task;
    }

    public GameProfile getGameProfile(){
        return gameProfile;
    }

    public String getUsername(){
        return gameProfile.getName();
    }

    public UUID getUUID(){
        return gameProfile.getId();
    }

    public boolean isDone(){
        for (CompletableFuture<?> future: tasks) if (!future.isDone()) return false;
        return true;
    }


}
