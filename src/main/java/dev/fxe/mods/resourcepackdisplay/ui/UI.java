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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
	private static IResourcePack pack;
	private final Minecraft mc = Minecraft.getMinecraft();
	private final ResourcePackRepository resourcePackRepository = mc.getResourcePackRepository();
	private final FontRenderer fontRenderer = mc.fontRendererObj;
	private final HashMap<String, String> packSize = new HashMap<>();
	private List<ResourcePackRepository.Entry> packs = resourcePackRepository.getRepositoryEntries();
	private ResourceLocation currentPack;

	public UI() {
		instance = this;
		loadTexture();
	}

	public static void drawPackDisplay() {
		GlStateManager.pushMatrix();
		if (pack == null) {
			pack = UI.instance.getCurrentPack();
		}
		int x = Config.x;
		int y = Config.y;
		final boolean pad = Config.hasPadding;
		int padding = pad ? 5 : 0;
		int height = 0;
		int offset = Config.displayPackIcon ? x + imageWidth : x;
		int textOffset = offset;
		textOffset += pad ? 5 : 0;
		int yOffset = 0;
		int w1, w2, w3;
		w1 = w2 = w3 = 0;
		if (Config.displayPackName) {
			height += 9;
			String s = pack.getPackName();
			w1 = UI.instance.fontRenderer.getStringWidth(s);
			UI.instance.fontRenderer.drawString(s,
				textOffset, y + padding + yOffset,
				0xffffff, Config.hasTextShadow);
			yOffset += 9;
		}
		if (Config.displayPackDescription) {
			height += 9;
			String s = UI.instance.getPackDescription(pack);
			w2 = UI.instance.fontRenderer.getStringWidth(s);
			UI.instance.fontRenderer.drawString(s, textOffset,
				y + yOffset + padding,
				0xffffff, Config.hasTextShadow);
			yOffset += 9;
		}
		if (Config.displayPackSize) {
			height += 9;
			String s = "Size: " + UI.instance.getPackSize(pack);
			w3 = UI.instance.fontRenderer.getStringWidth(s);
			UI.instance.fontRenderer.drawString(s, textOffset, y + yOffset + padding,
				0xffffff, Config.hasTextShadow);
		}

		if (Config.displayPackIcon) {
			height = 27;
			offset += 2;
			GlStateManager.color(1F, 1F, 1F, 1f);
			UI.instance.drawPackPng(padding);
		}

		if (Config.displayBackground) {
			GlStateManager.translate(1.0, 1.0, -100);
			offset += Math.max(w1, Math.max(w2, w3));
			offset += pad ? 9 : 0;
			height += pad ? 10 : 0;
			Gui.drawRect(x - 1, y - 1, offset, y + height - 2, Integer.MIN_VALUE);
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
		return "No Description";
	}

	private void drawPackPng(int padding) {
		int x = Config.x + padding;
		int y = Config.y + padding;
		int size = 26;
		mc.getTextureManager().bindTexture(this.currentPack);
		Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, size, size, 26F,
			26F);
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
		if (packs.size() > 0) {
			final IResourcePack resourcePack = packs.get(0).getResourcePack();
			final IResourcePack last = packs.get(packs.size() - 1).getResourcePack();
			if (Config.ignoreOverlay) {
				if (packs.size() == 1) {
					return mc.mcDefaultResourcePack;
				} else {
					return resourcePack;
				}
			} else {
				return last;
			}
		}
		return mc.mcDefaultResourcePack;
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
	public void render(RenderGameOverlayEvent.Post event) {
		if (Config.isGuiEnabled && event.type == RenderGameOverlayEvent.ElementType.ALL && (Minecraft.getMinecraft().currentScreen == null || Config.showInGui) && Minecraft.getMinecraft().thePlayer != null) {
			drawPackDisplay();
		}
	}

	@SubscribeEvent
	public void packChange(TextureStitchEvent.Post e) {
		packs = resourcePackRepository.getRepositoryEntries();
		pack = getCurrentPack();
		loadTexture();
	}

}
