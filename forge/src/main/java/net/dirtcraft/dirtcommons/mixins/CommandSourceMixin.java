package net.dirtcraft.dirtrestrict.mixins;

import net.dirtcraft.commons.VerifiableCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.item.minecart.CommandBlockMinecartEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockTileEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandSource.class)
public class CommandSourceMixin implements VerifiableCommandSource {

    @Shadow @Final private ICommandSource source;

    @Override
    public boolean isCommandCart() {
        return source instanceof CommandBlockMinecartEntity;
    }

    @Override
    public boolean isCommandBlock() {
        return source instanceof CommandBlockTileEntity;
    }

    @Override
    public boolean isConsole() {
        return source instanceof MinecraftServer;
    }

    @Override
    public boolean isPlayer() {
        return source instanceof ServerPlayerEntity;
    }

    @Override
    public ServerPlayerEntity getPlayer() {
        return (ServerPlayerEntity) source;
    }
}
