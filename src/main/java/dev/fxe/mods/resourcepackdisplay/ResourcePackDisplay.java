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

package dev.fxe.mods.resourcepackdisplay;

import club.sk1er.mods.core.gui.notification.Notifications;
import club.sk1er.mods.core.universal.ChatColor;
import club.sk1er.mods.core.util.MinecraftUtils;
import club.sk1er.mods.core.util.Multithreading;
import com.google.common.util.concurrent.ListenableFuture;
import dev.fxe.mods.resourcepackdisplay.commands.Command;
import dev.fxe.mods.resourcepackdisplay.data.Config;
import dev.fxe.mods.resourcepackdisplay.modcore.ModCoreInstaller;
import dev.fxe.mods.resourcepackdisplay.ui.UI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Mod(modid = ResourcePackDisplay.MOD_ID, name = ResourcePackDisplay.MOD_NAME, version = ResourcePackDisplay.VERSION)
public class ResourcePackDisplay {

    public static final String MOD_ID = "resource_pack_display";
    public static final String MOD_NAME = "Resource Pack Display";
    public static final String VERSION = "1.4-Beta";
    @Mod.Instance(MOD_ID)
    public static ResourcePackDisplay INSTANCE;
    public final HashSet<ResourcePackRepository.Entry> randomPacks = new HashSet<>();
    private final Config config = new Config();
    private final Minecraft mc = Minecraft.getMinecraft();


    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);
        this.config.preload();

        ClientCommandHandler.instance.registerCommand(new Command());
        MinecraftForge.EVENT_BUS.register(new UI());
    }


    public Config getConfig() {
        return config;
    }

    public void sendMessage(String message) {
        MinecraftUtils.sendMessage(ChatColor.BLUE + "[RPD] ",
            ChatColor.translateAlternateColorCodes('&', message));
    }

    public void selectRandomPack() {
        long start = System.currentTimeMillis();

        final ResourcePackRepository.Entry entry = this.randomPack();
        final String packName = entry.getResourcePackName();
        if (this.randomPacks.contains(entry)) {
            Notifications.INSTANCE.pushNotification(MOD_NAME, EnumChatFormatting.RED + "Duplicate pack selected: " + EnumChatFormatting.RESET+ packName);
            sendRandomPrompt();
        } else if (entry.func_183027_f() != 1) {
            Notifications.INSTANCE.pushNotification(MOD_NAME,
                EnumChatFormatting.RED + "Resource pack is not compatible! " + EnumChatFormatting.RESET + packName);
            sendRandomPrompt();
        } else {
            this.randomPacks.add(entry);
            final List<ResourcePackRepository.Entry> resourcePackListEntryList = new ArrayList<>();
            resourcePackListEntryList.add(entry);
            this.updatePack(resourcePackListEntryList);

            mc.gameSettings.resourcePacks.add(entry.getResourcePackName());
            mc.gameSettings.saveOptions();
            mc.gameSettings.resourcePacks.clear();
            ListenableFuture<Object> future = mc.scheduleResourcesRefresh();
            Multithreading.runAsync(() -> {
                while (!future.isDone()) ;
                long delay = Config.notifDelay;
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {
                }
                long end = System.currentTimeMillis();
                String msg = "Minecraft took " + ((end - delay) - start) / 1000 + "s to load " + packName;
                if (Config.notify) {
                    Notifications.INSTANCE.pushNotification(MOD_NAME, msg);
                }
                if (Config.chatNotification) {
                    sendMessage(msg);
                }
            });
        }
    }

    private void sendRandomPrompt() {
        ChatComponentText text = new ChatComponentText(ChatColor.BLUE + "[RPD] " +
            ChatColor.RESET + "Select a new pack?");
        ChatComponentText childComponent = new ChatComponentText(ChatColor.BLACK + " [" + ChatColor.GREEN +
            "Random" + ChatColor.BLACK + "] " + ChatColor.RESET);
        ChatComponentText childTwo = new ChatComponentText(ChatColor.BLACK + " [" + ChatColor.GREEN +
            "Reset" + ChatColor.BLACK + "] " + ChatColor.RESET);
        ChatStyle chatStyle = new ChatStyle();
        ChatStyle chatStyleTwo = new ChatStyle();
        final String cmd = "/rpd random";
        final String cmd2 = "/rpd reset";
        chatStyle.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
        chatStyle.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(cmd)));
        chatStyleTwo.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd2));
        chatStyleTwo.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(cmd2)));
        childComponent.setChatStyle(chatStyle);
        childTwo.setChatStyle(chatStyleTwo);
        text.appendSibling(childComponent);
        text.appendSibling(childTwo);
        MinecraftUtils.sendMessage(text);
    }

    private void updatePack(final List<ResourcePackRepository.Entry> resourcePackListEntryList) {
        final ResourcePackRepository resourcepackrepository = mc.getResourcePackRepository();
        resourcepackrepository.updateRepositoryEntriesAll();
        resourcepackrepository.setRepositories(resourcePackListEntryList);
    }

    private ResourcePackRepository.Entry randomPack() {
        final List<ResourcePackRepository.Entry> resourcePackListEntryList = mc.getResourcePackRepository().getRepositoryEntriesAll();
        final Random random = new Random();
        return resourcePackListEntryList.get(random.nextInt(resourcePackListEntryList.size()));
    }

    public void displaySelectedPacks() {
        StringBuilder packs = new StringBuilder();
        packs.append("All previously loaded packs").append("\n");
        for (ResourcePackRepository.Entry randomPack : randomPacks) {
            packs.append(randomPack.getResourcePackName()).append("\n");
        }
        sendMessage(packs.toString());
    }
}
