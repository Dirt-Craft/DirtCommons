package net.dirtcraft.dirtcommons;

import net.dirtcraft.dirtcommons.lib.AbstractDirtCommonsPlugin;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

@Mod("dirtcommon")
public class DirtCommons extends AbstractDirtCommonsPlugin {

    @Override
    public Path getBaseConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}
