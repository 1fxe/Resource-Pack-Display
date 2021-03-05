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

package dev.fxe.mods.resourcepackdisplay.ui

import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIImage
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.ChildBasedMaxSizeConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.state.BasicState
import dev.fxe.mods.resourcepackdisplay.ResourcePackDisplay.logger
import dev.fxe.mods.resourcepackdisplay.data.Config
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.resources.IResourcePack
import net.minecraft.client.resources.ResourcePackRepository
import net.minecraft.client.resources.data.PackMetadataSection
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.modcore.api.gui.hud.HudElement
import net.modcore.api.gui.hud.HudElementType
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author Filip
 */
class HUD() : HudElement(HUD) {
    companion object : HudElementType<HUD> {
        private var element = HUD()

        override fun constructElement() = element
        override fun getElementClass() = HUD::class.java
        override fun getElementName() = "Resource Pack Display"
        override fun getDescription() = "Displays your current resource pack"

    }

    private val packNameState = BasicState("Default")
    private val packDescState = BasicState("Default Look and Feel")
    private val packSizeState = BasicState("Size: N/A")
    private val mc = Minecraft.getMinecraft()
    private val resourcePackRepository = mc.resourcePackRepository
    private val packSizeMap = HashMap<String, String>()
    private var currentPack: ResourceLocation? = null;


    private val container = UIContainer()
        .constrain {
            width = ChildBasedMaxSizeConstraint()
            height = ChildBasedMaxSizeConstraint()
        } childOf this


    private val packName = UIText().bindText(packNameState)
        .constrain {
            x = Config.x.pixels() + 33.pixels()
            y = Config.y.pixels() + 1.pixels()
        } childOf container

    private val packDesc = UIText().bindText(packDescState)
        .constrain {
            x = Config.x.pixels() + 33.pixels()
            y = SiblingConstraint() + 1.pixels()
        } childOf container

    private val packSize = UIText().bindText(packSizeState)
        .constrain {
            x = Config.x.pixels() + 33.pixels()
            y = SiblingConstraint() + 1.pixels()
        } childOf container

    private val imageContainer = UIContainer()
        .constrain {
            x = Config.x.pixels()
            y = Config.y.pixels()
            width = 32.pixels()
            height = 32.pixels()
        } childOf container


    @SubscribeEvent
    fun join(e: EntityJoinWorldEvent) {
        if (e.entity.equals(mc.thePlayer)) {
            updateData()
        }
    }

    @SubscribeEvent
    fun packChange(e: TextureStitchEvent.Post) {
        updateData()
    }

    private fun updateData() {
        loadTexture();
        val pack = getCurrentPack();
        packNameState.set(pack.packName)
        packDescState.set(getPackDescription(pack))
        packSizeState.set(getPackSize(pack))

        imageContainer.children.clear()

        UIImage(CompletableFuture.supplyAsync {
            return@supplyAsync try {
                pack.packImage
            } catch (e: Exception) {
                BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR)
            }
        }).constrain {
            width = 100.percent()
            height = 100.percent()
        } childOf imageContainer


    }

    private fun loadTexture() {
        val dynamicTexture: DynamicTexture = try {
            DynamicTexture(getCurrentPack().packImage)
        } catch (e: Exception) {
            e.printStackTrace()
            TextureUtil.missingTexture
        }
        currentPack = mc.textureManager.getDynamicTextureLocation("texturepackicon", dynamicTexture)
    }

    private fun getCurrentPack(): IResourcePack {
        val packs: List<ResourcePackRepository.Entry> = resourcePackRepository.repositoryEntries
        return if (packs.isNotEmpty()) packs[0].resourcePack else mc.mcDefaultResourcePack
    }

    private fun getPackDescription(currentPack: IResourcePack): String {
        try {
            val packmetadatasection = currentPack.getPackMetadata<PackMetadataSection>(
                mc.resourcePackRepository.rprMetadataSerializer,
                "pack"
            )
            if (packmetadatasection != null) {
                return packmetadatasection.packDescription.formattedText
            }
        } catch (ignored: IOException) {
            logger.error("Failed to get pack metadata")
        }
        return ""
    }

    private fun getPackSize(pack: IResourcePack): String {
        val packName = pack.packName
        if (packSizeMap.containsKey(packName)) {
            return packSizeMap[packName].toString()
        }
        val packDir = File(mc.mcDataDir.absolutePath + File.separator + "resourcepacks")
        if (packDir.exists() && packDir.isDirectory) {
            val packFile =
                File(mc.mcDataDir.absolutePath + File.separator + "resourcepacks" + File.separator + packName)
            if (packFile.exists()) {
                val size: String = ((packFile.length() / 1024) / 1024).toString() + " MB"
                packSizeMap[packName] = size
                return size
            }
        }
        val s = "N/A"
        packSizeMap[packName] = s
        return s
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }
}