package com.cgh.openglhf.openglhf.client;

import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class TriangleRenderer {

    private final int vao;
    private final int vbo;
    private final ShaderProgram shaderProgram;

    public TriangleRenderer() throws Exception{
        float[] vertices = {
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                0.0f,  0.5f, 0.0f
        };

        vao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(vao);

        vbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertexBuffer, GL33.GL_STATIC_DRAW);
        MemoryUtil.memFree(vertexBuffer);

        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);

        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/assets/OpenGLHF/shaders/triangle.vert"));
        shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/triangle.frag"));
        shaderProgram.link();
    }

    public void render() {
        shaderProgram.bind();
        GL33.glBindVertexArray(vao);
        GL33.glDrawArrays(GL33.GL_TRIANGLES, 0, 3);
        shaderProgram.unbind();
    }

    public void cleanup() {
        GL33.glDisableVertexAttribArray(0);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
        GL33.glDeleteBuffers(vbo);
        GL33.glBindVertexArray(0);
        GL33.glDeleteVertexArrays(vao);
        shaderProgram.cleanup();
    }
}
