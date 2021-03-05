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

package dev.fxe.mods.resourcepackdisplay.data

import club.sk1er.vigilance.Vigilant
import club.sk1er.vigilance.data.Property
import club.sk1er.vigilance.data.PropertyType
import java.io.File

/**
 * @author Filip
 */
object Config : Vigilant(File("./config/rdp.toml")) {

    @Property(
        type = PropertyType.SWITCH,
        name = "Enable Gui",
        description = "Resource pack display gui",
        category = "GUI"
    )
    var isGuiEnabled = true

    @Property(type = PropertyType.SWITCH, name = "Display Pack background", category = "GUI")
    var displayBackground = false

    @Property(type = PropertyType.SWITCH, name = "Display Pack Icon", category = "GUI")
    var displayPackIcon = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Rounded Icon",
        description = "TODO: Makes the icon rounded",
        category = "GUI"
    )
    var roundIcon = false

    @Property(type = PropertyType.SWITCH, name = "Display Pack Name", category = "GUI")
    var displayPackName = false


    @Property(type = PropertyType.SWITCH, name = "Display Pack Description", category = "GUI")
    var displayPackDescription = false

    @Property(type = PropertyType.SWITCH, name = "Display Pack Size", category = "GUI")
    var displayPackSize = false

    @Property(type = PropertyType.SWITCH, name = "Pad the sides", category = "GUI")
    var hasPadding = false

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.NUMBER, name = "x", category = "GUI", hidden = true)
    var x = 0

    @kotlin.jvm.JvmField
    @Property(type = PropertyType.NUMBER, name = "x", category = "GUI", hidden = true)
    var y = 0


    init {
        initialize()
    }

}
