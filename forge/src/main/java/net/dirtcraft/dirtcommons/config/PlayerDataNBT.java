package net.dirtcraft.dirtcommons.config;

import net.dirtcraft.dirtcommons.ForgeCommons;
import net.dirtcraft.dirtcommons.command.CommandElement;
import net.minecraft.command.arguments.UUIDArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class PlayerDataNBT<T> {
    private final Path folder;
    private final Supplier<T> tFactory;
    private final Map<String, CommandElement<T>> commandElements = new LinkedHashMap<>();
    protected final Map<UUID, T> settings = new HashMap<>();

    public PlayerDataNBT(Path folder, Supplier<T> tFactory) {
        this.folder = folder;
        this.tFactory = tFactory;
        MinecraftForge.EVENT_BUS.addListener(this::onLogin);
        MinecraftForge.EVENT_BUS.addListener(this::onLogout);
    }

    public CompletableFuture<Void> modifyOffline(UUID player, Consumer<T> runnable) {
        ForgeCommons commons = ForgeCommons.getInstance();
        return CompletableFuture.runAsync(()->{
            if (!settings.containsKey(player)) load(player);
            try {
                runnable.accept(settings.computeIfAbsent(player, p -> tFactory.get()));
                save(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (commons.getPlayers().getOnlinePlayer(player) == null) settings.remove(player);
        }, commons.getScheduler().getAsyncExecutor());
    }

    public T getOrCreate(PlayerEntity player){
        return settings.computeIfAbsent(player.getGameProfile().getId(), p->tFactory.get());
    }

    public T getOrCreate(UUID player){
        return settings.computeIfAbsent(player, p->tFactory.get());
    }

    public T get(PlayerEntity player){
        return settings.get(player.getGameProfile().getId());
    }

    public T get(UUID player){
        return settings.get(player);
    }

    public T getOrDefault(PlayerEntity player, T def){
        return settings.getOrDefault(player.getGameProfile().getId(), def);
    }

    public T getOrDefault(UUID player, T def){
        return settings.getOrDefault(player, def);
    }

    public void unload(PlayerEntity player){
        save(player);
        settings.remove(player.getGameProfile().getId());
    }

    public void load(PlayerEntity player) {
        UUID playerId = player.getGameProfile().getId();
        this.load(playerId);
    }

    public void save(PlayerEntity player) {
        UUID playerId = player.getGameProfile().getId();
        this.save(playerId);

    }

    public void load(UUID player) {
        File data = getUserData(player);
        if (data.exists()) {
            try {
                CompoundNBT nbt = CompressedStreamTools.read(data);
                T settings = load(nbt);
                this.settings.put(player, settings);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void save(UUID player) {
        T settings = this.settings.get(player);
        if (settings == null) return;
        File data = getUserData(player);
        try {
            folder.toFile().mkdirs();
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
