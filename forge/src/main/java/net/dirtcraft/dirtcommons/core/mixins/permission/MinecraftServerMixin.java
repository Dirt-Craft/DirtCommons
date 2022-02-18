package net.dirtcraft.dirtcommons.core.mixins.permission;

import net.dirtcraft.dirtcommons.core.api.ForgeMessageReceiver;
import net.dirtcraft.dirtcommons.permission.Permissible;
import net.dirtcraft.dirtcommons.text.TextUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements Permissible, ForgeMessageReceiver {
    @Shadow public abstract void sendMessage(ITextComponent p_145747_1_, UUID p_145747_2_);

    @Override
    public boolean hasPermission(@NonNull String node) {
        return true;
    }

    @Override
    public void sendChatMessage(ITextComponent message) {
        sendMessage(message, Util.NIL_UUID);
    }

    @Override
    public void sendFormattedChatMessage(String message) {
        sendMessage(TextUtil.format(message), Util.NIL_UUID);
    }

    @Override
    public void sendPlainChatMessage(String message) {
        sendMessage(new StringTextComponent(message), Util.NIL_UUID);
    }

    @Override
    public void sendNotification(ITextComponent message) {
        sendMessage(message, Util.NIL_UUID);
    }

    @Override
    public void sendFormattedNotification(String message) {
        sendMessage(TextUtil.format(message), Util.NIL_UUID);
    }

    @Override
    public void sendPlainNotification(String message) {
        sendMessage(new StringTextComponent(message), Util.NIL_UUID);
    }
}
