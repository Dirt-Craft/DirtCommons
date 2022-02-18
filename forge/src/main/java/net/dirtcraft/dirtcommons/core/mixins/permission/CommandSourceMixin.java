package net.dirtcraft.dirtcommons.core.mixins.permission;

import net.dirtcraft.dirtcommons.core.api.ForgeCommandSource;
import net.dirtcraft.dirtcommons.core.api.ForgeMessageReceiver;
import net.dirtcraft.dirtcommons.permission.Permissible;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.ITextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(CommandSource.class)
public class CommandSourceMixin implements Permissible, ForgeMessageReceiver, ForgeCommandSource {
    @Shadow @Final private ICommandSource source;
    @Shadow @Final @Nullable private Entity entity;

    @Override
    public boolean hasPermission(@NonNull String node) {
        if (source instanceof Permissible) return ((Permissible)source).hasPermission(node);
        return entity instanceof Permissible && ((Permissible)entity).hasPermission(node);
    }

    @Override
    public void sendChatMessage(ITextComponent message) {
        if (source instanceof ForgeMessageReceiver) ((ForgeMessageReceiver) source).sendChatMessage(message);
    }

    @Override
    public void sendFormattedChatMessage(String message) {
        if (source instanceof ForgeMessageReceiver) ((ForgeMessageReceiver) source).sendFormattedChatMessage(message);
    }

    @Override
    public void sendPlainChatMessage(String message) {
        if (source instanceof ForgeMessageReceiver) ((ForgeMessageReceiver) source).sendPlainChatMessage(message);
    }

    @Override
    public void sendNotification(ITextComponent message) {
        if (source instanceof ForgeMessageReceiver) ((ForgeMessageReceiver) source).sendNotification(message);
    }

    @Override
    public void sendFormattedNotification(String message) {
        if (source instanceof ForgeMessageReceiver) ((ForgeMessageReceiver) source).sendFormattedNotification(message);
    }

    @Override
    public void sendPlainNotification(String message) {
        if (source instanceof ForgeMessageReceiver) ((ForgeMessageReceiver) source).sendPlainNotification(message);
    }
}
