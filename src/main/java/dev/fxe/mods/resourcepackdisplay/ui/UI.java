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

package dev.fxe.mods.resourcepackdisplay.ui;

import dev.fxe.mods.resourcepackdisplay.data.Config;
import dev.fxe.mods.resourcepackdisplay.data.Shaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.vecmath.Vector3f;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * @author Filip
 */
public class UI {
    private static final int imageWidth = 32;
    public static UI instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();
    private final FontRenderer fontRenderer = mc.fontRendererObj;
    private final HashMap<String, String> packSize = new HashMap<>();
    //    private static final HashMap<String, ResourceLocation> packTexture = new HashMap<>();
    private final ShaderManager shaderManager = new ShaderManager(Shaders.vert, Shaders.frag);
    private ResourceLocation currentPack;

    public UI() {
        instance = this;
        shaderManager.create();
        loadTexture();
    }

    public static void drawPackDisplay() {
        GlStateManager.pushMatrix();
        IResourcePack currentPack = UI.instance.getCurrentPack();
        int x = Config.x;
        int y = Config.y;
        int padding = 5;
        int height = 10;
        int offset = Config.displayPackIcon ? x + imageWidth + padding : x;

        if (Config.displayPackName) {
            height += 9;
            UI.instance.fontRenderer.drawString(currentPack.getPackName(),
                offset + padding, y + padding,
                0xffffff);
        }
        int yOffset = 11;

        if (Config.displayPackDescription) {
            height += 9;
            UI.instance.fontRenderer.drawString(UI.instance.getPackDescription(currentPack), offset + padding,
                y + yOffset + padding,
                0xffffff);
            yOffset += 11;
        }
        if (Config.displayPackSize) {
            height += 9;
            String size = UI.instance.getPackSize(currentPack);
            if (!size.equalsIgnoreCase(""))
                UI.instance.fontRenderer.drawString("Size: " + size, offset + padding, y + yOffset + padding,
                    0xffffff);
        }

        if (Config.displayPackIcon) {
            height = 39;
            GlStateManager.color(1F, 1F, 1F, 1f);
            UI.instance.drawPackPng(padding);
        }

        if (Config.displayBackground) {
            GlStateManager.translate(1.0, 1.0, -100);
            Gui.drawRect(x, y, offset + UI.instance.getWidth() + 10, y + height, Integer.MIN_VALUE);
            GlStateManager.translate(1.0, 1.0, 0);
        }

        GlStateManager.popMatrix();
    }

    private String getPackDescription(IResourcePack currentPack) {
        try {
            PackMetadataSection packmetadatasection = currentPack.getPackMetadata(this.mc.getResourcePackRepository().rprMetadataSerializer,
                "pack");
            if (packmetadatasection != null) {
                return packmetadatasection.getPackDescription().getFormattedText();
            }
        } catch (IOException ignored) {
        }
        return "";
    }

    private void drawPackPng(int padding) {
        int x = Config.x + padding;
        int y = Config.y + padding;
        int size = 32;
        if (Config.roundIcon) {
            // TODO fix round icon
            shaderManager.bind();
            mc.getTextureManager().bindTexture(this.currentPack);
            shaderManager.bindVec3("resolution", new Vector3f(size, size, 0));
            Gui.drawRect(x, y, x + size, y + size, Integer.MAX_VALUE);
            shaderManager.unbind();
        } else {
            mc.getTextureManager().bindTexture(this.currentPack);
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, size, size, 32.0F,
                32.0F);
        }
    }


    private void loadTexture() {
        DynamicTexture dynamicTexture;
        try {
            dynamicTexture = new DynamicTexture(getCurrentPack().getPackImage());
        } catch (Exception e) {
            dynamicTexture = TextureUtil.missingTexture;
        }
        this.currentPack = mc.getTextureManager().getDynamicTextureLocation("texturepackicon", dynamicTexture);
    }

    private IResourcePack getCurrentPack() {
        List<ResourcePackRepository.Entry> packs =
            resourcePackRepository.getRepositoryEntries();
        return packs.size() > 0 ? packs.get(0).getResourcePack() :
            mc.mcDefaultResourcePack;
    }

    private int getWidth() {
        IResourcePack pack = getCurrentPack();
        if (Config.displayPackName || Config.displayPackDescription) {
            int title = fontRenderer.getStringWidth(pack.getPackName());
            int description = fontRenderer.getStringWidth(UI.instance.getPackDescription(pack));
            return Math.max(title, description);
        }
        return -3;
    }

    private String getPackSize(IResourcePack pack) {
        final String packName = pack.getPackName();
        if (packSize.containsKey(packName)) {
            return packSize.get(packName);
        }

        File packDir = new File(mc.mcDataDir.getAbsolutePath() + File.separator + "resourcepacks");
        if (packDir.exists() && packDir.isDirectory()) {
            File packFile =
                new File(mc.mcDataDir.getAbsolutePath() + File.separator + "resourcepacks" + File.separator + packName);
            if (packFile.exists()) {
                String size = (packFile.length() / 1024) / 1024 + " MB";
                packSize.put(packName, size);
                return size;
            }
        }
        final String s = "N/A";
        packSize.put(packName, s);
        return s;
    }

    @SubscribeEvent
    public void render(TickEvent.RenderTickEvent event) {
        if (Config.isGuiEnabled && event.phase.equals(TickEvent.Phase.END) && Minecraft.getMinecraft().currentScreen == null) {
            drawPackDisplay();
        }
    }

    @SubscribeEvent
    public void packChange(TextureStitchEvent.Post e) {
        loadTexture();
    }

}
