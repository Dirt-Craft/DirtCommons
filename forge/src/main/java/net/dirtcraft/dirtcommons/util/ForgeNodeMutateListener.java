package net.dirtcraft.dirtcommons.util;

import net.dirtcraft.dirtcommons.core.api.ForgePlayer;
import net.dirtcraft.dirtcommons.user.PlayerList;
import net.luckperms.api.LuckPermsProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.function.Consumer;

public class ForgeNodeMutateListener extends AbstractNodeMutateListener<ForgePlayer> {

    public ForgeNodeMutateListener(Consumer<ForgePlayer> onUpdate) {
        super(onUpdate);
        try {
            init(LuckPermsProvider.get());
        } catch (Exception e) {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @Override
    protected PlayerList<ForgePlayer> getPlayerList() {
        //noinspection unchecked
        return (PlayerList<ForgePlayer>) ServerLifecycleHooks.getCurrentServer().getPlayerList();
    }

    @SubscribeEvent
    public void onServerStart(FMLServerStartedEvent event) {
        init(LuckPermsProvider.get());
    }
}
