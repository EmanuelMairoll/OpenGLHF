package com.cgh.openglhf.openglhf.client.renderer.custom;

import com.cgh.openglhf.openglhf.client.ShaderProgram;
import com.cgh.openglhf.openglhf.client.Utils;
import com.cgh.openglhf.openglhf.client.renderer.OpenGLHFRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL33;

public class EllipseRenderer implements OpenGLHFRenderer {

    private final int vao;
    private final ShaderProgram shaderProgram;
    private boolean renderingEnabled = false;

    private float majorAxis;
    private float minorAxis;

    private ShaderProgram.GLUniform majorAxisUniform;
    private ShaderProgram.GLUniform minorAxisUniform;

    public EllipseRenderer() {
        this(0.f, 0.f);
    }

    public EllipseRenderer(float majorAxis, float minorAxis) {

        this.majorAxis = majorAxis;
        this.minorAxis = minorAxis;

        vao = GL33.glGenVertexArrays();

        shaderProgram = new ShaderProgram();

        try {
            shaderProgram.createGeometryShader(Utils.loadResource("/assets/OpenGLHF/shaders/ellipse.geom"));
            shaderProgram.createVertexShader(Utils.loadResource("/assets/OpenGLHF/shaders/ellipse.vert"));
            shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/ellipse.frag"));
            shaderProgram.link();

            this.majorAxisUniform = shaderProgram.createGLUniformIfExists("majorAxis");
            this.minorAxisUniform = shaderProgram.createGLUniformIfExists("minorAxis");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRenderingEnabled() {
        return this.renderingEnabled;
    }

    @Override
    public void setRenderingEnabled(boolean enabled) {
        this.renderingEnabled = enabled;
    }

    @Override
    public void render(WorldRenderContext worldRenderContext) {
        if (!renderingEnabled || majorAxis <= 0.f || minorAxis <= 0.f) return;

        int prevVAO = GL33.glGetInteger(GL33.GL_VERTEX_ARRAY_BINDING);
        GL33.glBindVertexArray(vao);

        GL33.glDisable(GL33.GL_CULL_FACE);
        GL33.glDisable(GL33.GL_DEPTH_TEST);

        shaderProgram.bind();

        majorAxisUniform.setUniform1f(majorAxis);
        minorAxisUniform.setUniform1f(minorAxis);

        GL33.glDrawArrays(GL33.GL_POINTS, 0, 1);

        shaderProgram.unbind();

        GL33.glEnable(GL33.GL_CULL_FACE);
        GL33.glEnable(GL33.GL_DEPTH_TEST);

        GL33.glBindVertexArray(prevVAO);

    }

    public void setMinorAxis(float minorAxis) {
        this.minorAxis = minorAxis;
    }

    public void setMajorAxis(float majorAxis) {
        this.majorAxis = majorAxis;
    }
}
