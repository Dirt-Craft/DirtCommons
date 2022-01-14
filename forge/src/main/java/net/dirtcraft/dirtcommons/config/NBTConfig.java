package net.dirtcraft.commons.config;

import net.dirtcraft.commons.command.CommandElement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.server.permission.PermissionAPI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public abstract class NBTConfig<T> {
    private final String permission;
    private final Path folder;
    private final T DEFAULT;
    private final Map<String, CommandElement<T>> commandElements = new LinkedHashMap<>();
    private final Supplier<T> tFactory;
    protected final Map<PlayerEntity, T> settings = new HashMap<>();

    public NBTConfig(Path folder, Supplier<T> tFactory, T DEFAULT) {
        this(folder, tFactory, null, DEFAULT);
    }

    public NBTConfig(Path folder, Supplier<T> tFactory, String permission, T DEFAULT) {
        this.folder = folder;
        this.DEFAULT = DEFAULT;
        this.tFactory = tFactory;
        this.permission = permission;
        MinecraftForge.EVENT_BUS.addListener(this::onLogin);
        MinecraftForge.EVENT_BUS.addListener(this::onLogout);
    }

    public T getOrDummy(PlayerEntity player){
        return settings.getOrDefault(player, DEFAULT);
    }

    public void unload(PlayerEntity player){
        save(player);
        settings.remove(player);
    }

    public void load(PlayerEntity player) {
        if (permission != null && !PermissionAPI.hasPermission(player, permission)) return;
        UUID playerId = player.getGameProfile().getId();
        File data = getUserData(playerId);
        if (data.exists()) {
            try {
                CompoundNBT nbt = CompressedStreamTools.read(data);
                T settings = load(nbt);
                this.settings.put(player, settings);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            data.getParentFile().mkdirs();
            try {
                data.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.settings.put(player, tFactory.get());
            this.save(player);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save(PlayerEntity player) {
        T settings = this.settings.get(player);
        if (settings == null) return;
        UUID playerId = player.getGameProfile().getId();
        File data = getUserData(playerId);
        try {
            data.mkdirs();
            CompoundNBT nbt = new CompoundNBT();
            save(nbt, settings);
            CompressedStreamTools.write(nbt, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract T load(CompoundNBT nbt);

    protected abstract void save(CompoundNBT nbt, T obj);

    protected File getUserData(UUID uuid){
        return folder.resolve(uuid.toString() + ".dat").toFile();
    }

    public void onLogin(PlayerEvent.PlayerLoggedInEvent event){
        this.load(event.getPlayer());
    }

    public void onLogout(PlayerEvent.PlayerLoggedOutEvent event){
        this.unload(event.getPlayer());
    }

    @SafeVarargs
    protected final void registerElements(CommandElement<T>... elements) {
        for (CommandElement<T> element : elements) commandElements.put(element.getCommand(), element);
    }

    public CommandElement<T> getCommandElement(String cmd) {
        return commandElements.get(cmd.toLowerCase());
    }

    public Collection<CommandElement<T>> getCommandElements() {
        return commandElements.values();
    }

    public Collection<String> getCommands() {
        return commandElements.keySet();
    }


}
