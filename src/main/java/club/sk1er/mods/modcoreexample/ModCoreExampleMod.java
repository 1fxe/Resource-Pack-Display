package club.sk1er.mods.modcoreexample;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ModCoreExampleMod.MODID, name = ModCoreExampleMod.MOD_NAME, version = ModCoreExampleMod.VERSION)
public class ModCoreExampleMod {

    public static final String MODID = "modcore_example_mod";
    public static final String MOD_NAME = "ModCore Container Mod";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // install (if not found) & initialize ModCore on startup
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);
    }
}
