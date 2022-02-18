package net.dirtcraft.dirtcommons.core.mixins;

import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;
import net.dirtcraft.dirtcommons.core.api.ForgePlayer;
import net.dirtcraft.dirtcommons.text.TextUtil;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.lwjgl.system.CallbackI;
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
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements ForgePlayer, ForgePlayer.ChatManager {
    private ServerPlayerEntityMixin(World a, BlockPos b, float c, GameProfile d) {super(a,b,c,d); throw new Error("the fuck you doing?");}
    @Shadow public ServerPlayNetHandler connection;
    @Shadow public abstract void take(Entity p_71001_1_, int p_71001_2_);
    @Shadow public abstract boolean canHarmPlayer(PlayerEntity p_96122_1_);
    @Shadow public abstract void readAdditionalSaveData(CompoundNBT p_70037_1_);

    @Shadow public abstract void sendMessage(ITextComponent p_241151_1_, ChatType p_241151_2_, UUID p_241151_3_);

    @Shadow public abstract void magicCrit(Entity p_71047_1_);

    @Unique private boolean team$glowing;
    @Unique private LegacyColors team$color;
    @Unique private boolean vanish$wallHacking;
    @Unique private Set<Entity> vanish$tracking;
    @Unique private short vanish$viewLevel;
    @Unique private short vanish$level;
    @Unique private User permission$user;
    @Unique private ITextComponent chat$carat;
    @Unique private ITextComponent chat$prefix;
    @Unique private ITextComponent chat$indicator;
    @Unique private ITextComponent chat$display;
    @Unique private ITextComponent chat$suffix;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void constructor(MinecraftServer p_i45285_1_, ServerWorld p_i45285_2_, GameProfile p_i45285_3_, PlayerInteractionManager p_i45285_4_, CallbackInfo ci){
        vanish$tracking = new HashSet<>();
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
        this.chat$carat = sep.chat$carat;
        this.chat$prefix = sep.chat$prefix;
        this.chat$indicator = sep.chat$indicator;
        this.chat$display = sep.chat$display;
        this.chat$suffix = sep.chat$suffix;
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
    public ITextComponent getUserDisplayCarat() {
        return chat$carat;
    }

    @Override
    public ITextComponent getUserDisplayPrefix() {
        return chat$prefix;
    }

    @Override
    public ITextComponent getUserDisplayIndicator() {
        return chat$indicator;
    }

    @Override
    public ITextComponent getUserDisplayName() {
        return chat$display;
    }

    @Override
    public ITextComponent getUserDisplaySuffix() {
        return chat$suffix;
    }


    @Override
    public void setUserDisplayCarat(ITextComponent chat$carat) {
        this.chat$carat = chat$carat;
    }

    @Override
    public void setUserDisplayPrefix(ITextComponent chat$prefix) {
        this.chat$prefix = chat$prefix;
    }

    @Override
    public void setUserDisplayIndicator(ITextComponent chat$indicator) {
        this.chat$indicator = chat$indicator;
    }

    @Override
    public void setUserDisplayName(ITextComponent chat$display) {
        this.chat$display = chat$display;
    }

    @Override
    public void setUserDisplaySuffix(ITextComponent chat$suffix) {
        this.chat$suffix = chat$suffix;
    }

    @Override
    public void sendChatMessage(ITextComponent message) {
        sendMessage(message, ChatType.CHAT, Util.NIL_UUID);
    }

    @Override
    public void sendFormattedChatMessage(String message) {
        sendMessage(TextUtil.format(message), ChatType.CHAT, Util.NIL_UUID);
    }

    @Override
    public void sendPlainChatMessage(String message) {
        sendMessage(new StringTextComponent(message), ChatType.CHAT, Util.NIL_UUID);
    }

    @Override
    public void sendNotification(ITextComponent message) {
        sendMessage(message, ChatType.GAME_INFO, Util.NIL_UUID);
    }

    @Override
    public void sendFormattedNotification(String message) {
        sendMessage(TextUtil.format(message), ChatType.GAME_INFO, Util.NIL_UUID);
    }

    @Override
    public void sendPlainNotification(String message) {
        sendMessage(new StringTextComponent(message), ChatType.GAME_INFO, Util.NIL_UUID);
    }
}
