package net.dirtcraft.vanish;

import net.dirtcraft.commons.text.Colors;
import net.dirtcraft.commons.text.Styles;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;

public interface FBIAgent {
    boolean isGlowAgent();

    void setGlowAgent(boolean value);

    boolean isWallHacking();

    void setWallHacking(boolean value);

    boolean isTracking(Entity entity);

    void addTrackedEntities(Collection<? extends Entity> entities);
    void removeTrackedEntities(Collection<? extends Entity> entities);
    void clearTrackedEntities();

    IFormattableTextComponent getPrefix();

    default IFormattableTextComponent getSuffix() {
        return getVanishLevel() > 0? new StringTextComponent("[")
                .append(new StringTextComponent("VANISH").withStyle(Styles.as(Colors.BLACK)))
                .append("]")
                .withStyle(Styles.as(Colors.GREY)) : null;
    }

    void setPrefix(IFormattableTextComponent prefix);

    TextFormatting getColor();

    void setColor(TextFormatting color);

    String fbi$getName();

    short getVanishViewLevel();

    void setVanishViewLevel(short v);

    short getVanishLevel();

    void setVanishLevel(short v);
}
