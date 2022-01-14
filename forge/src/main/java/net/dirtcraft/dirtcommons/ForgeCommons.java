package net.dirtcraft.commons;

import com.mojang.authlib.GameProfile;
import net.dirtcraft.commons.config.ColorSerializer;
import net.dirtcraft.commons.config.ItemSerializer;
import net.dirtcraft.commons.config.WorldSerializer;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.util.Tristate;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.*;

public class ForgeCommons extends Commons {

    public ForgeCommons(){
        registerDefaultSerializer(Item.class, new ItemSerializer());
        registerDefaultSerializer(World.class, new WorldSerializer());
        registerDefaultSerializer(Color.class, new ColorSerializer());
    }

    protected boolean hasPermission(@NonNull GameProfile profile, @NonNull String node) {
        try {
            User user = LuckPermsProvider.get().getUserManager().getUser(profile.getId());
            if (user == null) return false;

            @NonNull Tristate result = user.getCachedData().getPermissionData().checkPermission(node);
            return result.asBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkPerms(CommandSource source, String node) {
        VerifiableCommandSource s = (VerifiableCommandSource) source;
        if (s.isCommandBlock() || s.isCommandCart() || s.isConsole()) return true;
        else if (!s.isPlayer()) return false;
        ServerPlayerEntity player = s.getPlayer();
        return hasPermission(player.getGameProfile(), node);
    }
}
