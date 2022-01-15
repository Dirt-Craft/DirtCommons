package net.dirtcraft.dirtcommons.core.mixins;

import com.mojang.authlib.GameProfile;
import net.dirtcraft.dirtcommons.core.api.CommonsPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements CommonsPlayer {
    @Shadow public ServerPlayNetHandler connection;

    @Shadow public abstract void take(Entity p_71001_1_, int p_71001_2_);

    private ServerPlayerEntityMixin(World a, BlockPos b, float c, GameProfile d) {super(a,b,c,d); throw new Error("the fuck you doing?");}
    @Unique private Set<Entity> fbi$tracking;
    @Unique private boolean fbi$glowAgent;
    @Unique private boolean fbi$wallHacking;
    @Unique private IFormattableTextComponent fbi$prefix;
    @Unique private TextFormatting fbi$color;
    @Unique private short vanishViewLevel;
    @Unique private short vanishLevel;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(MinecraftServer p_i45285_1_, ServerWorld p_i45285_2_, GameProfile p_i45285_3_, PlayerInteractionManager p_i45285_4_, CallbackInfo ci){
        fbi$tracking = new HashSet<>();
    }

    @Override
    public boolean isGlowAgent() {
        return fbi$glowAgent;
    }

    @Override
    public void setGlowAgent(boolean value) {
        this.fbi$glowAgent = value;
    }

    @Override
    public boolean isWallHacking() {
        return fbi$wallHacking;
    }

    @Override
    public void setWallHacking(boolean value){
        this.fbi$wallHacking = value;
        sendGlowUpdatePackets(level.players());
    }

    @Override
    public boolean isTracking(Entity entity) {
        return fbi$tracking.contains(entity);
    }

    @Override
    public void addTrackedEntities(Collection<? extends Entity> entities) {
        this.fbi$tracking.addAll(entities);
        this.fbi$tracking.remove(this);
        sendGlowUpdatePackets(entities);
    }

    @Override
    public void removeTrackedEntities(Collection<? extends Entity> entities) {
        this.fbi$tracking.removeAll(entities);
        sendGlowUpdatePackets(entities);
    }

    @Override
    public void clearTrackedEntities() {
        Set<Entity> old = fbi$tracking;
        this.fbi$tracking = new HashSet<>();
        sendGlowUpdatePackets(old);
    }

    @Unique
    private void sendGlowUpdatePackets(Collection<? extends Entity> entities) {
        entities.forEach(e-> connection.send(new SEntityMetadataPacket(e.getId(), e.getEntityData(), true)));
    }

    @Override
    public IFormattableTextComponent getPrefix() {
        return fbi$prefix;
    }

    @Override
    public void setPrefix(IFormattableTextComponent prefix) {
        this.fbi$prefix = prefix;
    }

    @Override
    public TextFormatting getColor() {
        return fbi$color;
    }

    @Override
    public void setColor(TextFormatting color) {
        this.fbi$color = color;
    }

    @Override
    public String fbi$getName() {
        return this.getGameProfile().getName();
    }

    @Override
    public short getVanishViewLevel() {
        return vanishViewLevel;
    }

    @Override
    public void setVanishViewLevel(short v) {
        vanishViewLevel = v;
    }

    @Override
    public short getVanishLevel() {
        return vanishLevel;
    }

    @Override
    public void setVanishLevel(short v) {
        vanishLevel = v;
    }
}
