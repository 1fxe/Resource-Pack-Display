/*
 * MIT License
 *
 * Copyright (c) 2021. 1fxe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.fxe.mods.resourcepackdisplay.commands

import dev.fxe.mods.resourcepackdisplay.ResourcePackDisplay.randomPacks
import dev.fxe.mods.resourcepackdisplay.ResourcePackDisplay.selectRandomPack
import dev.fxe.mods.resourcepackdisplay.ResourcePackDisplay.sendMessage
import dev.fxe.mods.resourcepackdisplay.data.Config
import dev.fxe.mods.resourcepackdisplay.ui.GUI
import net.modcore.api.ModCoreAPI
import net.modcore.api.commands.Command
import net.modcore.api.commands.DefaultHandler
import net.modcore.api.commands.SubCommand
import net.modcore.api.utils.GuiUtil

/**
 * @author Filip
 */
class Command : Command("rdp") {

    @DefaultHandler
    fun handle() {
        val gui = Config.gui() ?: error("Couldn't open RDP config")
        GuiUtil.open(gui)
    }

    @SubCommand("gui", description = "Opens the draggable gui")
    fun openDragGui() {
        ModCoreAPI.getGuiUtil().openScreen(GUI())
    }

    @SubCommand("random", description = "Loads a random resource pack")
    fun random() {
        selectRandomPack()
    }

    @SubCommand("reset", description = "If you are getting to many duplicates reset")
    fun reset() {
        randomPacks.clear()
        sendMessage("&cCleared Duplicate Checker")
    }

}
