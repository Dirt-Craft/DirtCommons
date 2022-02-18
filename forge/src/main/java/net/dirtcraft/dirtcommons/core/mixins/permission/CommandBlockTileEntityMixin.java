package net.dirtcraft.dirtcommons.core.mixins.permission;

import net.dirtcraft.dirtcommons.permission.Permissible;
import net.minecraft.tileentity.CommandBlockTileEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CommandBlockTileEntity.class)
public class CommandBlockTileEntityMixin implements Permissible {
    @Override
    public boolean hasPermission(@NonNull String node) {
        return true;
    }
}
