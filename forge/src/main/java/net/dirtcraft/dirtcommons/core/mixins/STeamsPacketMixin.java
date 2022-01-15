package net.dirtcraft.dirtcommons.core.mixins;

import net.dirtcraft.dirtcommons.core.api.CustomTeamPacket;
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
    @Shadow private ITextComponent playerPrefix;
    @Shadow private ITextComponent playerSuffix;
    @Shadow private TextFormatting color;
    @Shadow private String name;
    @Shadow @Final private Collection<String> players;


    @Override
    public STeamsPacket setData(IFormattableTextComponent prefix, IFormattableTextComponent suffix, TextFormatting color, String... player) {
        if (prefix != null) this.playerPrefix = prefix;
        if (suffix != null) this.playerSuffix = prefix;
        if (color != null) this.color = color;
        this.name = UUID.randomUUID().toString().substring(0, 15);
        this.players.addAll(Arrays.asList(player));
        return (STeamsPacket) (Object) this;
    }
}
