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

package dev.fxe.mods.resourcepackdisplay.data;

import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;

import java.io.File;

/**
 * @author Filip
 */
public class Config extends Vigilant {

    @Property(
        type = PropertyType.SWITCH, name = "Enable Gui",
        description = "Resource pack display gui", category = "GUI"
    )
    public static boolean isGuiEnabled;

    @Property(
        type = PropertyType.SWITCH, name = "Display Pack background",
        category = "GUI"
    )
    public static boolean displayBackground;

    @Property(
        type = PropertyType.SWITCH, name = "Display Pack Icon",
        category = "GUI"
    )
    public static boolean displayPackIcon;

    @Property(
        type = PropertyType.SWITCH, name = "Rounded Icon",
        description = "TODO: Makes the icon rounded", category = "GUI"
    )
    public static boolean roundIcon;

    @Property(
        type = PropertyType.SWITCH, name = "Display Pack Name",
        category = "GUI")
    public static boolean displayPackName;


    @Property(
        type = PropertyType.SWITCH, name = "Display Pack Description",
        category = "GUI")
    public static boolean displayPackDescription;

    @Property(
        type = PropertyType.SWITCH, name = "Display Pack Size",
        category = "GUI")
    public static boolean displayPackSize;


    @Property(type = PropertyType.NUMBER, name = "x", category = "GUI", hidden = true)
    public static int x = 0;

    @Property(type = PropertyType.NUMBER, name = "x", category = "GUI", hidden = true)
    public static int y = 0;

    public Config() {
        super(new File("./config/rdp.toml"));
        initialize();
    }
}
