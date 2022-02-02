package net.dirtcraft.dirtcommons.core.mixins;

import net.dirtcraft.dirtcommons.core.api.CustomTeamPacket;
import net.dirtcraft.dirtcommons.core.api.ForgePlayer;
import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.dirtcraft.dirtcommons.util.ColorUtils;
import net.dirtcraft.dirtcommons.util.LegacyColors;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@Mixin(STeamsPacket.class)
public class STeamsPacketMixin implements CustomTeamPacket {
    @Shadow private TextFormatting color;
    @Shadow private String name;
    @Shadow @Final private Collection<String> players;

    @Override
    public STeamsPacket setData(LegacyColors color, String... player) {
        if (color != null) this.color = ColorUtils.fromLegacy(color);
        this.name = UUID.randomUUID().toString().substring(0, 15);
        this.players.addAll(Arrays.asList(player));
        return (STeamsPacket) (Object) this;
    }

    @Override
    public STeamsPacket setData(CommonsPlayer<?,?,?> player) {
        return setData(player.getColor(), player.getUserName());
    }
}
