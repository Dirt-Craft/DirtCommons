package net.dirtcraft.dirtcommons.core.api;

import net.dirtcraft.dirtcommons.user.CommonsPlayer;
import net.dirtcraft.dirtcommons.util.ColorUtils;
import net.dirtcraft.dirtcommons.util.LegacyColors;
import net.minecraft.network.play.server.STeamsPacket;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public interface CustomTeamPacket {

    static CustomTeamPacket getInstance(){
        return (CustomTeamPacket) new STeamsPacket();
    }

    STeamsPacket setData(LegacyColors color, String... player);

    STeamsPacket setData(CommonsPlayer<?,?> player);

}
