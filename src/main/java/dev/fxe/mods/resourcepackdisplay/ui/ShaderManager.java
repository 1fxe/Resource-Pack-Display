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

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class ShaderManager {
    private String vertexFile, fragFile;
    private int vID, fID, program;

/*	public ShaderManager(String vertexFile, String fragFile) {
		this.vertexFile = FileUtils.loadAsString(vertexFile);
		this.fragFile = FileUtils.loadAsString(fragFile);

	}*/

    public ShaderManager(String vertexFile, String fragFile) {
        this.vertexFile = vertexFile;
        this.fragFile = fragFile;

    }

    public void create() {
        program = GL20.glCreateProgram();
        vID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

        GL20.glShaderSource(vID, this.vertexFile);
        GL20.glCompileShader(vID);

        if (didCompile(vID)) {
            return;
        }

        fID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        GL20.glShaderSource(fID, this.fragFile);
        GL20.glCompileShader(fID);

        if (didCompile(fID)) {
            return;
        }

        GL20.glAttachShader(program, vID);
        GL20.glAttachShader(program, fID);
        GL20.glLinkProgram(program);

        if (didProgram(GL20.GL_LINK_STATUS)) {
            return;
        }
        GL20.glValidateProgram(program);
        if (didProgram(GL20.GL_VALIDATE_STATUS)) {
            return;
        }
        GL20.glDeleteShader(vID);
        GL20.glDeleteShader(fID);
    }

    public void bind() {
        GL20.glUseProgram(program);
    }

    public void unbind() {
        GL20.glUseProgram(0);
    }

    public void destroy() {
        GL20.glDeleteProgram(program);
    }


    public void bindVec2(String s, Vector2f v) {
        int a = GL20.glGetUniformLocation(program, s);
        GL20.glUniform2f(a, v.getX(), v.getY());
    }

    public void bindVec3(String s, Vector3f v) {
        int a = GL20.glGetUniformLocation(program, s);
        GL20.glUniform3f(a, v.getX(), v.getY(), v.getZ());
    }

    public void bindTexture(String s, int id) {
        int a = GL20.glGetUniformLocation(program, s);
        GL20.glUniform1i(a, 0);
    }

    public void bindFloat(String s, float f) {
        int a = GL20.glGetUniformLocation(program, s);
        GL20.glUniform1f(a, f);
    }


    public void bindInt(String s, int frame) {
        int a = GL20.glGetUniformLocation(program, s);
        GL20.glUniform1i(a, frame);
    }

    public boolean didCompile(int id) {
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Could not compile shader:  " + GL20.glGetShaderInfoLog(id, 100));
            return true;
        }
        return false;
    }

    public boolean didProgram(int id) {
        if (GL20.glGetProgrami(program, id) == GL11.GL_FALSE) {
            System.err.println("Failed to link shader program " + GL20.glGetProgramInfoLog(program, 100));
            return true;
        }
        return false;
    }

}
