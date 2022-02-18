package net.dirtcraft.dirtcommons.util;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcommons.core.api.ForgePlayer;
import net.dirtcraft.dirtcommons.exceptions.PermissionException;
import net.dirtcraft.dirtcommons.permission.Permissible;
import net.minecraft.command.CommandSource;

public class CommandUtil {
    public static ForgePlayer playerWithPermission(CommandContext<CommandSource> ctx, String node) throws CommandSyntaxException {
        ForgePlayer p = (ForgePlayer) ctx.getSource().getPlayerOrException();
        if (!p.hasPermission(node)) throw new PermissionException(node);
        return p;
    }

    public static boolean hasPermission(CommandSource src, String node) {
        return src instanceof Permissible && ((Permissible)src).hasPermission(node);
    }

    public static boolean hasPermission(CommandSource ctx, String node, int op) {
        return hasPermission(ctx, node) || ctx.hasPermission(op);
    }

    public static boolean hasPermission(CommandContext<CommandSource> ctx, String node) {
        return hasPermission(ctx.getSource(), node);
    }

    public static boolean hasPermission(CommandContext<CommandSource> ctx, String node, int op) {
        return hasPermission(ctx.getSource(), node) || ctx.getSource().hasPermission(op);
    }

    public static void assertPermission(CommandContext<CommandSource> ctx, String node) {
        if (!hasPermission(ctx, node)) throw new PermissionException(node);
    }

    public static void assertPermission(CommandContext<CommandSource> ctx, String node, int op) {
        if (!hasPermission(ctx, node, op)) throw new PermissionException(node);
    }
}
