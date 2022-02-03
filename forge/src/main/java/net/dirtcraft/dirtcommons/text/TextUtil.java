package net.dirtcraft.dirtcommons.text;

import net.dirtcraft.dirtcommons.core.api.ForgePlayer;
import net.dirtcraft.dirtcommons.permission.Permissions;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TextUtil {
    public static final char FORMAT_CHAR = '&';
    public static final Pattern FORMATTING_REGEX = Pattern.compile(FORMAT_CHAR + "([k-oi0-9a-f]|[#%][0-9a-f]{6}|\\$\\d)");
    private static final Map<Character, net.minecraft.util.text.TextFormatting> FORMATTING_MAP = new HashMap<>();
    static {
        for (net.minecraft.util.text.TextFormatting f : net.minecraft.util.text.TextFormatting.values()) {
            FORMATTING_MAP.put(f.code, f);
        }
    }

    public static ITextComponent format(String in, ITextComponent... args){
        return format(null, in, FORMAT_CHAR, args);
    }

    public static ITextComponent format(String in, char formattingCode, ITextComponent... args) {
        return format(null, in, formattingCode, args);
    }


    public static ITextComponent format(ForgePlayer player, String in, ITextComponent... args) {
        return format(player, in, FORMAT_CHAR, args);
    }

    public static ITextComponent format(@Nullable ForgePlayer src, String in, char formattingCode, ITextComponent... args) {
        if (in == null || in.isEmpty()) return null;
        List<IFormattableTextComponent> parts = null;
        IFormattableTextComponent base = new StringTextComponent("");
        StringBuilder builder = new StringBuilder();
        Style style = Style.EMPTY;
        boolean colors = src == null || src.hasPermission(Permissions.COLORS_USE);
        boolean format = src == null || src.hasPermission(Permissions.COLORS_FORMAT);
        boolean hex = src == null    || src.hasPermission(Permissions.COLORS_HEX);
        boolean newline = src == null|| src.hasPermission(Permissions.COLORS_STAFF);
        boolean applyFormatting = false;
        boolean gradientMode = false;
        Color gradient = null;

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            char next = charAt(in, i + 1, '.');
            if (c == formattingCode) {
                if (next == c) {
                    applyFormatting = true;
                    builder.append(c);
                    i++;
                    continue;
                } else if (next == '^') {
                    i++;
                    applyFormatting = true;
                    if (newline) builder.append("\n");
                    continue;
                } else if (applyFormatting) {
                    ITextComponent segment = new StringTextComponent(builder.toString()).withStyle(style);
                    base.append(segment);
                    style = Style.EMPTY;
                    builder.delete(0, builder.length());
                    applyFormatting = false;
                }
                net.minecraft.util.text.TextFormatting legacy = FORMATTING_MAP.get(next);
                if (legacy != null) {
                    i++;
                    if (legacy.isFormat() ? format : colors) style = style.applyFormat(legacy);
                } else switch (next) {
                    case '#': {
                        if (hex) style = style.withColor(parseRgb(i + 2, in));
                        i += 7;
                        break;
                    } case '%': {
                        Color color = parseRgb(i + 2, in);
                        i += 7;
                        if (hex) style = style.withColor(color);
                        if (gradientMode) {
                            base.append(parseGradient(gradient, color, parts));
                            parts.clear();
                        } else {
                            if (parts == null) parts = new ArrayList<>();
                            gradient = color;
                        }
                        gradientMode = !gradientMode;
                        break;
                    } case '$': {
                        i+=2;
                        boolean prefixSpace = false;
                        boolean suffixSpace = false;
                        char element = charAt(in, i, '-');
                        if (element == '<') {
                            i++;
                            element = charAt(in, i, '-');
                            prefixSpace = true;
                        }
                        if (element == '>') {
                            i++;
                            element = charAt(in, i, '-');
                            suffixSpace = true;
                        }
                        int index = Character.isDigit(element)? Character.getNumericValue(element): -1;
                        if (index < args.length && args[index] != null) {
                            if (prefixSpace) base.append(" ");
                            base.append(args[index]);
                            if (suffixSpace) base.append(" ");
                        }
                        break;
                    } case 'i': {
                        i++;
                        List<ITextComponent> text = base.getSiblings();
                        if (text.size() > 0) style = text.get(text.size() - 1).getStyle();
                        if (!(src instanceof ServerPlayerEntity)) continue;
                        ServerPlayerEntity player = (ServerPlayerEntity) src;
                        ITextComponent item = player.getItemInHand(player.getUsedItemHand()).getDisplayName();
                        base.append(item);
                        break;
                    } default:
                }
            } else if (gradientMode) {
                parts.add(new StringTextComponent(Character.toString(c)).withStyle(style));
            } else {
                applyFormatting = true;
                builder.append(c);
            }
        }
        if (builder.length() == 0) return base;
        ITextComponent segment = new StringTextComponent(builder.toString()).withStyle(style);
        base.append(segment);
        return base;
    }

    public static String stripFormatting(String text){
        return FORMATTING_REGEX.matcher(text).replaceAll("");
    }

    public static char charAt(String in, int i, char outOfBounds) {
        return i < in.length() ? in.charAt(i) : outOfBounds;
    }

    public static Color parseRgb(String hex) {
        try {
            int rgb = Integer.decode("0x" + hex);
            return Color.fromRgb(rgb);
        } catch (NumberFormatException ignored){}
        return Color.fromRgb(0xFFFFFFFF);
    }

    private static Color parseRgb(int i, String in) {
        try {
            return parseRgb(in.substring(i, i + 6));
        } catch (IndexOutOfBoundsException ignored){}
        return Color.fromRgb(0xFFFFFFFF);
    }

    private static ITextComponent parseGradient(Color start, Color finish, List<IFormattableTextComponent> parts){
        StringTextComponent component = new StringTextComponent("");
        int r1 = start.value >> 16 & 0x000000FF;
        int g1 = start.value >> 8  & 0x000000FF;
        int b1 = start.value       & 0x000000FF;

        int r2 = finish.value >> 16 & 0x000000FF;
        int g2 = finish.value >> 8  & 0x000000FF;
        int b2 = finish.value       & 0x000000FF;

        double rStep = r1 == r2? 0D: (double) (r1 - r2) / parts.size() - 1;
        double gStep = g1 == g2? 0D: (double) (g1 - g2) / parts.size() - 1;
        double bStep = b1 == b2? 0D: (double) (b1 - b2) / parts.size() - 1;
        for (int i = 0; i < parts.size(); i++) {
            IFormattableTextComponent t = parts.get(i);
            int ca = (r1 - (int) (i * rStep) & 0x000000FF) << 16;
            int cb = (g1 - (int) (i * gStep) & 0x000000FF) << 8;
            int cc = (b1 - (int) (i * bStep) & 0x000000FF);
            t.withStyle(Style.EMPTY.withColor(Color.fromRgb(ca | cb | cc)));
            component.append(t);
        }
        return component;
    }
}
