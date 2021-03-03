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

/**
 * @author Filip
 */
public class Shaders {

    public static final String vert = "void main(){\n" +
        "   gl_Position = ftransform();\n" +
        "}";

    public static final String frag = "uniform vec3 resolution;\n" +
        "\n" +
        "void main() {\n" +
        "    vec2 uv = gl_FragCoord.xy/resolution.xy;\n" +
        "    vec2 center = uv - vec2(0.5,0.5);\n" +
        "    center.x = center.x * (resolution.x / resolution.y);\n" +
        "    vec3 col = vec3(0.9, 0.3 + center.y, 0.5 + center.x);\n" +
        "    float r = 0.5;\n" +
        "    col *= 1.0 - smoothstep(r, r + 0.01, length(center));\n" +
        "    gl_FragColor = vec4(col, 1.0);\n" +
        "}";
}
