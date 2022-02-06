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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    public static final Pattern FORMATTING_REGEX = Pattern.compile("(?i)[&§]([k-oi0-9a-f]|[#%x][0-9a-f]{6}|\\$\\d|x([&§][0-9a-f]){6})");
    private static final Pattern SPIGOT_GRADIENTS = Pattern.compile("(?i)(?<start>&%[0-9a-f]{6})(?<format>(&[k-o0-9a-f])+?)(?<text>.+?)(?<end>&%[0-9a-f]{6})");
    private static final Map<Character, net.minecraft.util.text.TextFormatting> FORMATTING_MAP = new HashMap<>();
    static {
        for (net.minecraft.util.text.TextFormatting f : net.minecraft.util.text.TextFormatting.values()) {
            FORMATTING_MAP.put(f.code, f);
        }
    }

    public static ITextComponent format(String in, ITextComponent... args){
        return format(null, in, true, args);
    }


    public static ITextComponent format(ForgePlayer player, String in, ITextComponent... args) {
        return format(player, in, false, args);
    }


    public static ITextComponent format(@Nullable ForgePlayer src, String in, boolean colorized, ITextComponent... args) {
        if (in == null || in.isEmpty()) return null;
        List<IFormattableTextComponent> parts = null;
        IFormattableTextComponent base = new StringTextComponent("");
        StringBuilder builder = new StringBuilder();
        Style style = Style.EMPTY;
        boolean colors =  colorized || src == null || src.hasPermission(Permissions.COLORS_USE);
        boolean format =  colorized || src == null || src.hasPermission(Permissions.COLORS_FORMAT);
        boolean hex =     colorized || src == null || src.hasPermission(Permissions.COLORS_HEX);
        boolean newline = colorized || src == null || src.hasPermission(Permissions.COLORS_STAFF);
        boolean applyFormatting = false;
        boolean gradientMode = false;
        Color gradient = null;

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            char next = charAt(in, i + 1, '.');
            if (c == '&' || c == '§') {
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
                    } case 'x': {
                        style.withColor(parseSpigotRgb(i + 2, in));
                        i += 13;
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
                    } case '$': {                        i+=2;
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
                        if (element == ':') {
                            int st = ++i;
                            while (charAt(in, i, ':') != ':') i+=1;
                            String s = in.substring(st, i);
                            String meta = src == null? null : src.getMeta(s);
                            ITextComponent comp = format(meta);
                            if (comp != null && prefixSpace) base.append(" ");
                            if (comp != null) base.append(comp);
                            if (comp != null && suffixSpace) base.append(" ");
                            break;
                        }
                        int index = Character.isDigit(element)? Character.getNumericValue(element): -1;
                        if (index >= 0 && index < args.length && args[index] != null) {
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
        if (parts != null && !parts.isEmpty()) {
            base.append(parseGradient(gradient, Color.fromRgb(0x00FFFFFF), parts));
            parts.clear();
        } else if (builder.length() == 0) return base;
        ITextComponent segment = new StringTextComponent(builder.toString()).withStyle(style);
        base.append(segment);
        return base;
    }

    /*
    private static int parseArgument(String in, int i, IFormattableTextComponent component, ITextComponent... args) {
        char ch;
        boolean ps = false;
        boolean ss = false;
        while ((ch = charAt(in, ++i, '§')) != '§') switch (ch) {
            case '<': ps = true; break;
            case '>': ss = true; break;
            default: {
                if (!Character.isDigit(ch)) break;
                int index = Character.getNumericValue(ch);
                ITextComponent element = args[index];
                if (element != null && ps) component.append(" ");
                if (element != null) component.append(element);
                if (element != null && ss) component.append(" ");
                return i;
            }
        }
        return i;
    }
     */

    public static String stripFormatting(String text){
        return FORMATTING_REGEX.matcher(text).replaceAll("");
    }

    public static char charAt(String in, int i, char outOfBounds) {
        return i < in.length() ? in.charAt(i) : outOfBounds;
    }

    public static String formatSpigotFriendlyGradients(String in) {
        Matcher m = SPIGOT_GRADIENTS.matcher(in);
        while (m.find()) {
            Color gradientStart = parseRgb(m.group("start").substring(2));
            Color gradientEnd = parseRgb(m.group("end").substring(2));
            String format = m.group("format");
            if (format == null) format = "";
            else format = format.replace('&', '§');
            String text = m.group("text");
            int[] rgb = parseGradient(gradientStart, gradientEnd, text.length());
            StringBuilder f = new StringBuilder();
            for (int i = 0; i < rgb.length; i++) {
                int color = rgb[i];
                char ch = text.charAt(i);
                f.append("§");
                f.append("x");
                f.append(String.format("%1$06X", color).replaceAll("(.)", "§$1"));
                f.append(format);
                f.append(ch);
            }
            in = m.replaceFirst(f.toString());
            m = SPIGOT_GRADIENTS.matcher(in);
        }

        return in;
    }

    private static ITextComponent parseGradient(Color start, Color finish, List<IFormattableTextComponent> parts){
        StringTextComponent component = new StringTextComponent("");
        int[] colors = parseGradient(start, finish, parts.size());
        for (int i = 0; i < parts.size(); i++) {
            //System.out.printf("%1$06X\n", colors[i]);
            IFormattableTextComponent t = parts.get(i);
            Color color = Color.fromRgb(colors[i]);
            t.withStyle(Style.EMPTY.withColor(color));
            component.append(t);
        }
        return component;
    }

    private static int[] parseGradient(Color start, Color finish, int chunks){
        int[] colors = new int[chunks];
        int r1 = start.value >> 16 & 0x000000FF;
        int g1 = start.value >> 8  & 0x000000FF;
        int b1 = start.value       & 0x000000FF;

        int r2 = finish.value >> 16 & 0x000000FF;
        int g2 = finish.value >> 8  & 0x000000FF;
        int b2 = finish.value       & 0x000000FF;

        double rStep = r1 == r2? 0D: (double) (r1 - r2) / (chunks -1);
        double gStep = g1 == g2? 0D: (double) (g1 - g2) / (chunks -1);
        double bStep = b1 == b2? 0D: (double) (b1 - b2) / (chunks -1);
        for (int i = 0; i < chunks; i++) {
            int ca = Math.min(r1 - (int) (i * rStep), 0x000000FF) << 16;
            int cb = Math.min(g1 - (int) (i * gStep), 0x000000FF) << 8;
            int cc = Math.min(b1 - (int) (i * bStep), 0x000000FF);
            colors[i] = (ca | cb | cc);
        }
        return colors;
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

    private static Color parseSpigotRgb(int i, String in) {
        try {
            return parseRgb(in.substring(i, i + 12).replaceAll("§", ""));
        } catch (IndexOutOfBoundsException ignored){}
        return Color.fromRgb(0xFFFFFFFF);
    }
}
