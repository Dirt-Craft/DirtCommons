package net.dirtcraft.dirtcommons.core.mixins;

import net.dirtcraft.dirtcommons.core.api.EntityGlowPacket;
import net.dirtcraft.dirtcommons.core.api.CommonsPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SEntityMetadataPacket.class)
public class SEntityMetadataPacketMixin implements EntityGlowPacket {
    @Unique private ServerPlayerEntity viewer;

    private static final DataParameter<Byte> DATA_SHARED_FLAGS_ID = Entity.DATA_SHARED_FLAGS_ID;
    @Shadow private List<EntityDataManager.DataEntry<?>> packedItems;

    @Shadow private int id;

    @Inject(method = "write", at = @At("HEAD"))
    public void onWrite(PacketBuffer p_148840_1_, CallbackInfo ci){
        packedItems.forEach(item->{
            if (item.getAccessor() == DATA_SHARED_FLAGS_ID && canView()) forceGlow((EntityDataManager.DataEntry<Byte>)item, true);
        });
    }

    @Unique
    private boolean canView() {
        if (viewer == null) return false;
        Entity subject = viewer.level.getEntity(id);
        CommonsPlayer viewerSettings = (CommonsPlayer) viewer;
        if (viewerSettings.isTracking(subject)) return true;
        if (!(subject instanceof CommonsPlayer)) return false;
        CommonsPlayer subjectSettings = (CommonsPlayer) subject;
        return viewerSettings.isWallHacking() || subjectSettings.isGlowAgent();
    }

    @Unique
    @Override
    public void addViewerData(ServerPlayerEntity entity) {
        this.viewer = entity;
    }

    @Unique
    @Override
    public void forceGlow(EntityDataManager.DataEntry<Byte> data, boolean value) {
        byte current = data.getValue();
        if (value) {
            data.setValue((byte)(current | 1 << 6));
        } else {
            data.setValue((byte)(current & ~(1 << 6)));
        }

    }
}
