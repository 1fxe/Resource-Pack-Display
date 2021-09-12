package dev.fxe.mods.resourcepackdisplay.commands;

import dev.fxe.mods.resourcepackdisplay.ResourcePackDisplay;
import dev.fxe.mods.resourcepackdisplay.ui.GUI;
import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.commands.SubCommand;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

public class RPDCommand extends Command {
    @Nullable
    @Override
    public Set<Alias> getCommandAliases() {
        return Collections.singleton(new Alias("rpd"));
    }

    @DefaultHandler
    public void handle() {
        EssentialAPI.getGuiUtil().openScreen(ResourcePackDisplay.INSTANCE.getConfig().gui());
    }

    @SubCommand(value = "random", description = "Selects a completely random resource pack (excluding duplicates) and applies it.")
    public void random() {
        ResourcePackDisplay.INSTANCE.selectRandomPack();
    }

    @SubCommand(value = "gui", aliases = {"hud"}, description = "Opens the GUI to the HUD editor, to move it around.")
    public void gui() {
        EssentialAPI.getGuiUtil().openScreen(new GUI());
    }

    @SubCommand(value = "reset", description = "Reset the duplicates checker for the random argument.")
    public void reset() {
        ResourcePackDisplay.INSTANCE.randomPacks.clear();
        ResourcePackDisplay.INSTANCE.sendMessage("&cCleared Duplicate Checker");
    }

    @SubCommand(value = "show", description = "Outputs all previous random packs used in the session.")
    public void show() {
        ResourcePackDisplay.INSTANCE.displaySelectedPacks();
    }

    public RPDCommand() {
        super("resourcepackdisplay", true);
    }
}
