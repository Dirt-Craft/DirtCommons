package net.dirtcraft.dirtcommons.util;

import net.minecraft.util.text.TextFormatting;

public class ColorUtils {
    public static TextFormatting fromLegacy(LegacyColors colors){
        switch (colors) {
            case BLACK: return TextFormatting.BLACK;
            case DARK_BLUE: return TextFormatting.DARK_BLUE;
            case DARK_GREEN: return TextFormatting.DARK_GREEN;
            case DARK_AQUA: return TextFormatting.DARK_AQUA;
            case DARK_RED: return TextFormatting.DARK_RED;
            case DARK_PURPLE: return TextFormatting.DARK_PURPLE;
            case GOLD: return TextFormatting.GOLD;
            case GRAY: return TextFormatting.GRAY;
            case DARK_GRAY: return TextFormatting.DARK_GRAY;
            case BLUE: return TextFormatting.BLUE;
            case GREEN: return TextFormatting.GREEN;
            case AQUA: return TextFormatting.AQUA;
            case RED: return TextFormatting.RED;
            case LIGHT_PURPLE: return TextFormatting.LIGHT_PURPLE;
            case YELLOW: return TextFormatting.YELLOW;
            case WHITE: return TextFormatting.WHITE;
            case OBFUSCATED: return TextFormatting.OBFUSCATED;
            case BOLD: return TextFormatting.BOLD;
            case STRIKETHROUGH: return TextFormatting.STRIKETHROUGH;
            case UNDERLINE: return TextFormatting.UNDERLINE;
            case ITALIC: return TextFormatting.ITALIC;
            default: return TextFormatting.RESET;
        }
    }
    public static LegacyColors toLegacy(TextFormatting colors){
        switch (colors) {
            case BLACK: return LegacyColors.BLACK;
            case DARK_BLUE: return LegacyColors.DARK_BLUE;
            case DARK_GREEN: return LegacyColors.DARK_GREEN;
            case DARK_AQUA: return LegacyColors.DARK_AQUA;
            case DARK_RED: return LegacyColors.DARK_RED;
            case DARK_PURPLE: return LegacyColors.DARK_PURPLE;
            case GOLD: return LegacyColors.GOLD;
            case GRAY: return LegacyColors.GRAY;
            case DARK_GRAY: return LegacyColors.DARK_GRAY;
            case BLUE: return LegacyColors.BLUE;
            case GREEN: return LegacyColors.GREEN;
            case AQUA: return LegacyColors.AQUA;
            case RED: return LegacyColors.RED;
            case LIGHT_PURPLE: return LegacyColors.LIGHT_PURPLE;
            case YELLOW: return LegacyColors.YELLOW;
            case WHITE: return LegacyColors.WHITE;
            case OBFUSCATED: return LegacyColors.OBFUSCATED;
            case BOLD: return LegacyColors.BOLD;
            case STRIKETHROUGH: return LegacyColors.STRIKETHROUGH;
            case UNDERLINE: return LegacyColors.UNDERLINE;
            case ITALIC: return LegacyColors.ITALIC;
            default: return LegacyColors.RESET;
        }
    }
}
