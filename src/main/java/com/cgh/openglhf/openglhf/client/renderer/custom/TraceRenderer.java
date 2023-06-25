package com.cgh.openglhf.openglhf.client.renderer.custom;

import com.cgh.openglhf.openglhf.client.ShaderProgram;
import com.cgh.openglhf.openglhf.client.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL33;

import java.util.stream.DoubleStream;

public class TraceRenderer extends AbstractPosRenderer {

    private ShaderProgram.GLUniform majorAxisUniform;
    private ShaderProgram.GLUniform minorAxisUniform;
    private ShaderProgram.GLUniform viewportDimensionsUniform;
    private ShaderProgram.GLUniform ellipseTestUniform;

    private float majorAxis = 0.f;
    private float minorAxis = 0.f;
    private boolean ellipseTest = false;

    @Override
    protected void configureBuffers(int vao, int vbo) {
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 16, 0);
        GL33.glEnableVertexAttribArray(0);

        GL33.glVertexAttribPointer(1, 1, GL33.GL_FLOAT, false, 16, 12);
        GL33.glEnableVertexAttribArray(1);
    }

    @Override
    protected ShaderProgram makeShader() throws Exception {
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.vert"));
        shaderProgram.createGeometryShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.geom"));
        shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.frag"));
        shaderProgram.link();

        this.majorAxisUniform = shaderProgram.createGLUniformIfExists("majorAxis");
        this.minorAxisUniform = shaderProgram.createGLUniformIfExists("minorAxis");
        this.viewportDimensionsUniform = shaderProgram.createGLUniformIfExists("viewportDimensions");
        this.ellipseTestUniform = shaderProgram.createGLUniformIfExists("ellipseTest");

        return shaderProgram;
    }


    @Override
    protected DoubleStream toVertices(Entity entity, float tickDelta) {

        var distance = entity.distanceTo(MinecraftClient.getInstance().player);

        return DoubleStream.of(
                MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()),
                MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()),
                MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()),
                distance);
    }

    @Override
    public void updateUniforms() {
        int[] dimensions = new int[4];
        GL33.glGetIntegerv(GL33.GL_VIEWPORT, dimensions);
        this.viewportDimensionsUniform.setVec2i(dimensions[2], dimensions[3]);
        this.minorAxisUniform.setUniform1f(minorAxis);
        this.majorAxisUniform.setUniform1f(majorAxis);
        this.ellipseTestUniform.setBool(ellipseTest && (majorAxis > 0) && (minorAxis > 0));
    }

    public void setEllipseTestMinorAxis(float minorAxis) {
        this.minorAxis = minorAxis;
    }

    public void setEllipseTestMajorAxis(float majorAxis) {
        this.majorAxis = majorAxis;
    }

    public void setEllipseTestEnabled(boolean ellipseTest) {
        this.ellipseTest = ellipseTest;
    }

    public boolean isEllipseTestEnabled() {
        return this.ellipseTest;
    }

    protected void glDrawArrays(int length) {
        GL33.glDrawArrays(GL33.GL_POINTS, 0, length / 4);
    }

}
