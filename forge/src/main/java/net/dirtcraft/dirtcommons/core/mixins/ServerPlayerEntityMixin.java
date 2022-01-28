package net.dirtcraft.dirtcommons.core.mixins;

import com.mojang.authlib.GameProfile;
import net.dirtcraft.dirtcommons.core.api.ForgePlayer;
import net.dirtcraft.dirtcommons.util.LegacyColors;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
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
import java.util.UUID;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ForgePlayer {
    private ServerPlayerEntityMixin(World a, BlockPos b, float c, GameProfile d) {super(a,b,c,d); throw new Error("the fuck you doing?");}

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Shadow public ServerPlayNetHandler connection;
    @Shadow public abstract void take(Entity p_71001_1_, int p_71001_2_);

    @Shadow public abstract boolean canHarmPlayer(PlayerEntity p_96122_1_);

    @Unique private boolean team$glowing;
    @Unique private ITextComponent team$prefix;
    @Unique private ITextComponent team$suffix;
    @Unique private LegacyColors team$color;
    @Unique private boolean vanish$wallHacking;
    @Unique private Set<Entity> vanish$tracking;
    @Unique private short vanish$viewLevel;
    @Unique private short vanish$level;
    @Unique private User permission$user;
    @Unique private String chat$nickname;
    @Unique private ITextComponent chat$displayName;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(MinecraftServer p_i45285_1_, ServerWorld p_i45285_2_, GameProfile p_i45285_3_, PlayerInteractionManager p_i45285_4_, CallbackInfo ci){
        vanish$tracking = new HashSet<>();
    }

    @Override
    public String getUserName(){
        return getGameProfile().getName();
    }

    @Override
    public UUID getUserId(){
        return getGameProfile().getId();
    }

    @Override
    public boolean isUserGlowing() {
        return team$glowing;
    }

    @Override
    public void setUserGlowing(boolean value) {
        this.team$glowing = value;
    }

    @Override
    public LegacyColors getColor() {
        return team$color;
    }

    @Override
    public void setColor(LegacyColors color) {
        this.team$color = color;
    }

    @Override
    public boolean canSeePlayerOutlines() {
        return vanish$wallHacking;
    }

    @Override
    public void setSeePlayerOutlines(boolean value){
        this.vanish$wallHacking = value;
        sendGlowUpdatePackets(level.players());
    }

    @Override
    public boolean isTracking(Entity entity) {
        return vanish$tracking.contains(entity);
    }

    @Override
    public void addTrackedEntities(Collection<? extends Entity> entities) {
        this.vanish$tracking.addAll(entities);
        this.vanish$tracking.remove(this);
        sendGlowUpdatePackets(entities);
    }

    @Override
    public void removeTrackedEntities(Collection<? extends Entity> entities) {
        this.vanish$tracking.removeAll(entities);
        sendGlowUpdatePackets(entities);
    }

    @Override
    public void clearTrackedEntities() {
        Set<Entity> old = vanish$tracking;
        this.vanish$tracking = new HashSet<>();
        sendGlowUpdatePackets(old);
    }

    @Unique
    private void sendGlowUpdatePackets(Collection<? extends Entity> entities) {
        entities.forEach(e-> connection.send(new SEntityMetadataPacket(e.getId(), e.getEntityData(), true)));
    }

    @Override
    public short getVanishViewLevel() {
        return vanish$viewLevel;
    }

    @Override
    public void setVanishViewLevel(short v) {
        vanish$viewLevel = v;
    }

    @Override
    public short getVanishLevel() {
        return vanish$level;
    }

    @Override
    public void setVanishLevel(short v) {
        vanish$level = v;
    }

    @Override
    public ServerPlayerEntity getServerEntity() {
        return (ServerPlayerEntity) (Object) this;
    }

    @Override
    public User getUser() {
        if (permission$user == null) this.permission$user = LuckPermsProvider.get()
                .getUserManager()
                .getUser(getGameProfile().getId());
        return permission$user;
    }

    @Override
    public ITextComponent getPrefix() {
        return this.team$prefix;
    }

    @Override
    public void setPrefix(ITextComponent prefix) {
        this.team$prefix = prefix;
    }

    @Override
    public ITextComponent getSuffix() {
        return team$suffix;
    }

    @Override
    public void setSuffix(ITextComponent suffix) {
        this.team$suffix = suffix;
    }

    public String getNickname() {
        return this.chat$nickname;
    }

    public void setNickname(String nick){
        this.chat$nickname = nick;
    }

    @Override
    public ITextComponent getDisplayName() {
        return chat$displayName;
    }

    @Override
    public void setDisplayName(ITextComponent name) {
        this.chat$displayName = name;
    }
}
