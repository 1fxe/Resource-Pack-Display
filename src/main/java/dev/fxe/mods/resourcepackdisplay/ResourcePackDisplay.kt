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
package dev.fxe.mods.resourcepackdisplay

import club.sk1er.mods.core.universal.ChatColor
import club.sk1er.mods.core.universal.wrappers.message.UTextComponent
import dev.fxe.mods.resourcepackdisplay.ResourcePackDisplay.MOD_NAME
import dev.fxe.mods.resourcepackdisplay.commands.Command
import dev.fxe.mods.resourcepackdisplay.data.Config
import dev.fxe.mods.resourcepackdisplay.ui.HUD
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.ResourcePackRepository
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.modcore.api.ModCoreAPI
import net.modcore.api.utils.Multithreading
import org.apache.logging.log4j.LogManager
import java.util.*

@Mod(
    modid = "resource_pack_display",
    name = MOD_NAME,
    version = "1.0",
    modLanguageAdapter = "net.modcore.api.utils.KotlinAdapter"
)
object ResourcePackDisplay {
    val randomPacks = HashSet<ResourcePackRepository.Entry>()
    private val mc = Minecraft.getMinecraft()
    const val MOD_NAME = "Resource Pack Display"
    val logger = LogManager.getLogger(MOD_NAME)

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        logger.info("Loading configuration")
        Config.preload()
        logger.info("Registering command")
        ModCoreAPI.getCommandRegistry().registerCommand(Command())
        logger.info("Registering HUD Element")
        ModCoreAPI.getHudRegistry().registerElement(HUD)
    }

    fun sendMessage(message: String) {
        ModCoreAPI.getMinecraftUtil().sendMessage(
            ChatColor.BLUE.toString() + "[RDP] ",
            ChatColor.translateAlternateColorCodes('&', message)
        )
    }

    fun selectRandomPack() {
        val start = System.currentTimeMillis()
        val entry = randomPack()
        val packName = entry.resourcePackName

        when {
            randomPacks.contains(entry) -> {
                ModCoreAPI.getNotifications().push(
                    MOD_NAME,
                    "Duplicate pack selected: $packName"
                )
                sendRandomPrompt()
            }
            entry.func_183027_f() != 1 -> {
                ModCoreAPI.getNotifications().push(
                    MOD_NAME,
                    "Resource pack is not compatible! $packName"
                )
                sendRandomPrompt()
            }
            else -> {
                randomPacks.add(entry)
                val resourcePackListEntryList: MutableList<ResourcePackRepository.Entry> = ArrayList()
                resourcePackListEntryList.add(entry)
                updatePack(resourcePackListEntryList)
                mc.gameSettings.resourcePacks.add(entry.resourcePackName)
                mc.gameSettings.saveOptions()
                mc.gameSettings.resourcePacks.clear()
                val future = mc.scheduleResourcesRefresh()
                Multithreading.runAsync(Runnable {
                    while (!future.isDone);
                    val end: Long = System.currentTimeMillis()
                    ModCoreAPI.getNotifications().push(
                        MOD_NAME,
                        "Minecraft took " + ((end - start) / 1000) + "s to load " + packName
                    )
                })
            }
        }
    }

    private fun sendRandomPrompt() {
        val text = UTextComponent(
            ChatColor.BLUE.toString() + "[RDP] " +
                    ChatColor.RESET + "Select a new pack?"
        )
        val childComponent = ChatComponentText(
            (ChatColor.BLACK.toString() + " [" + ChatColor.GREEN +
                    "Random" + ChatColor.BLACK + "] " + ChatColor.RESET)
        )
        val childTwo = ChatComponentText(
            (ChatColor.BLACK.toString() + " [" + ChatColor.GREEN +
                    "Reset" + ChatColor.BLACK + "] " + ChatColor.RESET)
        )
        val chatStyle = ChatStyle()
        val chatStyleTwo = ChatStyle()
        val cmd = "/rdp random"
        val cmd2 = "/rdp reset"
        chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd)
        chatStyle.chatHoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText(cmd))
        chatStyleTwo.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd2)
        chatStyleTwo.chatHoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText(cmd2))
        childComponent.chatStyle = chatStyle
        childTwo.chatStyle = chatStyleTwo
        text.appendSibling(childComponent)
        text.appendSibling(childTwo)
        ModCoreAPI.getMinecraftUtil().sendMessage(text)
    }

    private fun updatePack(resourcePackListEntryList: List<ResourcePackRepository.Entry>) {
        val resourcepackrepository = mc.resourcePackRepository
        resourcepackrepository.updateRepositoryEntriesAll()
        resourcepackrepository.setRepositories(resourcePackListEntryList)
    }

    private fun randomPack(): ResourcePackRepository.Entry {
        val resourcePackListEntryList = mc.resourcePackRepository.repositoryEntriesAll
        val random = Random()
        return resourcePackListEntryList[random.nextInt(resourcePackListEntryList.size)]
    }


}