/*
 * Copyright (c) 2021. 1fxe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package dev.fxe.mods.resourcepackdisplay.commands;

import club.sk1er.mods.core.ModCore;
import dev.fxe.mods.resourcepackdisplay.ResourcePackDisplay;
import dev.fxe.mods.resourcepackdisplay.ui.GUI;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

/**
 * @author Filip
 */
public class Command extends CommandBase {

    @Override
    public String getCommandName() {
        return "rpd";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/" + getCommandName() + "\n<random>\n<gui>\n<reset>\n<show>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            ModCore.getInstance().getGuiHandler().open(ResourcePackDisplay.INSTANCE.getConfig().gui());
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("random")) {
                ResourcePackDisplay.INSTANCE.selectRandomPack();
            } else if (args[0].equalsIgnoreCase("gui")) {
                ModCore.getInstance().getGuiHandler().open(new GUI());
            } else if (args[0].equalsIgnoreCase("reset")) {
                ResourcePackDisplay.INSTANCE.randomPacks.clear();
                ResourcePackDisplay.INSTANCE.sendMessage("&cCleared Duplicate Checker");
            } else if (args[0].equalsIgnoreCase("show")) {
                ResourcePackDisplay.INSTANCE.displaySelectedPacks();
            } else {
                ResourcePackDisplay.INSTANCE.sendMessage("&cIncorrect arguments. Command usage is: " + getCommandUsage(sender));
            }
        } else {
            ResourcePackDisplay.INSTANCE.sendMessage("&cIncorrect arguments. Command usage is: " + getCommandUsage(sender));
        }

    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
