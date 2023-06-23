package com.cgh.openglhf.openglhf.client.renderer.custom;

import com.cgh.openglhf.openglhf.client.ShaderProgram;
import com.cgh.openglhf.openglhf.client.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL33;

import java.util.stream.DoubleStream;

public class TraceRenderer extends AbstractPosRenderer {

    @Override
    protected ShaderProgram makeShader() throws Exception {
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.vert"));
        shaderProgram.createGeometryShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.geom"));
        shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.frag"));
        shaderProgram.link();
        return shaderProgram;
    }

    @Override
    protected DoubleStream toVertices(Entity entity, float tickDelta) {
        if (entity instanceof SheepEntity) {
            return DoubleStream.of(
                    MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()),
                    MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()),
                    MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()));
        } else {
            return DoubleStream.of();
        }
    }

    protected void glDrawArrays(int length) {
        GL33.glDrawArrays(GL33.GL_POINTS, 0, length);
    }
}
