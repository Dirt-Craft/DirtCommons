package net.dirtcraft.dirtcommons;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.dirtcraft.dirtcommons.config.ColorSerializer;
import net.dirtcraft.dirtcommons.config.ItemSerializer;
import net.dirtcraft.dirtcommons.config.WorldSerializer;
import net.dirtcraft.dirtcommons.core.api.ForgePlayer;
import net.dirtcraft.dirtcommons.permission.Permissions;
import net.dirtcraft.dirtcommons.threads.ForgeThreadManager;
import net.dirtcraft.dirtcommons.threads.ThreadManager;
import net.dirtcraft.dirtcommons.user.PlayerList;
import net.dirtcraft.dirtcommons.util.PermissionHelper;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.util.Tristate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.awt.*;
import java.nio.file.Path;

@Mod(ForgeCommons.MOD_ID)
public class ForgeCommons extends Commons<ForgePlayer> {
    public static final String MOD_ID = "dirtcommons";
    private static ForgeCommons INSTANCE;
    private PermissionHelper permissionHelper = new PermissionHelper();
    private ForgeThreadManager threadManager = new ForgeThreadManager();

    public static Path getConfigPath(String modId) {
        return FMLPaths.GAMEDIR.get().resolve(FMLPaths.CONFIGDIR.get()).resolve(modId);
    }

    public static ForgeCommons getInstance(){
        return INSTANCE;
    }

    public ForgeCommons(){
        this.INSTANCE = this;
        //FMLJavaModLoadingContext.get().getModEventBus().register(this);
        registerDefaultSerializer(Item.class, new ItemSerializer());
        registerDefaultSerializer(World.class, new WorldSerializer());
        registerDefaultSerializer(Color.class, new ColorSerializer());

    }

    @Override
    public Permissions getPermissionHelper() {
        return permissionHelper;
    }

    @Override
    public ThreadManager getScheduler() {
        return threadManager;
    }

    @Override
    public PlayerList<ForgePlayer> getPlayers() {
        //noinspection unchecked
        return (PlayerList<ForgePlayer>) ServerLifecycleHooks.getCurrentServer().getPlayerList();
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
