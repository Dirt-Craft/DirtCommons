package net.dirtcraft.dirtcommons.core.mixins;

import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;
import net.dirtcraft.dirtcommons.core.api.ForgePlayer;
import net.dirtcraft.dirtcommons.util.LegacyColors;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ForgePlayer {
    private ServerPlayerEntityMixin(World a, BlockPos b, float c, GameProfile d) {super(a,b,c,d); throw new Error("the fuck you doing?");}
    @Shadow public ServerPlayNetHandler connection;
    @Shadow public abstract void take(Entity p_71001_1_, int p_71001_2_);
    @Shadow public abstract boolean canHarmPlayer(PlayerEntity p_96122_1_);
    @Shadow public abstract void readAdditionalSaveData(CompoundNBT p_70037_1_);

    @Unique private boolean team$glowing;
    @Unique private LegacyColors team$color;
    @Unique private boolean vanish$wallHacking;
    @Unique private Set<Entity> vanish$tracking;
    @Unique private short vanish$viewLevel;
    @Unique private short vanish$level;
    @Unique private User permission$user;
    @Unique private ITextComponent chat$displayChat;
    @Unique private ITextComponent chat$displayTab;
    @Unique private ITextComponent chat$displayComp;
    @Unique private ITextComponent chat$display;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(MinecraftServer p_i45285_1_, ServerWorld p_i45285_2_, GameProfile p_i45285_3_, PlayerInteractionManager p_i45285_4_, CallbackInfo ci){
        vanish$tracking = new HashSet<>();
    }

    @Inject(method = "getTabListDisplayName", at = @At("HEAD"), cancellable = true)
    public void getTabListDisplayName(CallbackInfoReturnable<ITextComponent> cir) {
        cir.setReturnValue(getUserTabListDisplayName());
    }

    @Inject(method = "restoreFrom", at = @At("HEAD"))
    public void onRestore(ServerPlayerEntity p_193104_1_, boolean p_193104_2_, CallbackInfo ci){
        ServerPlayerEntityMixin sep = (ServerPlayerEntityMixin) (Object) p_193104_1_;
        this.team$glowing = sep.team$glowing;
        this.team$color = sep.team$color;
        this.vanish$wallHacking = sep.vanish$wallHacking;
        this.vanish$viewLevel = sep.vanish$viewLevel;
        this.vanish$level = sep.vanish$level;
        this.permission$user = sep.permission$user;
        this.chat$displayChat = sep.chat$displayChat;
        this.chat$displayTab = sep.chat$displayTab;
        this.chat$display = sep.chat$display;
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
    public void setUserChatDisplayName(ITextComponent name) {
        chat$displayChat = name;
    }

    @Override
    public void setUserTabListDisplayName(ITextComponent name) {
        chat$displayTab = name;
    }

    @Override
    public void setUserCompactDisplayName(ITextComponent name) {
        chat$displayComp = name;
    }

    @Override
    public ITextComponent getUserChatDisplayName() {
        return chat$displayChat == null? this.getDisplayName() : chat$displayChat;
    }

    @Override
    public ITextComponent getUserTabListDisplayName() {
        return chat$displayTab == null? this.getDisplayName() : chat$displayTab;
    }

    @Override
    public ITextComponent getUserCompactDisplayName() {
        return chat$displayComp == null? this.getDisplayName() : chat$displayComp;
    }
}
