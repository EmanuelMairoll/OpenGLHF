package com.cgh.openglhf.openglhf.client.renderer.custom;

import com.cgh.openglhf.openglhf.client.ShaderProgram;
import com.cgh.openglhf.openglhf.client.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL33;

import java.util.stream.DoubleStream;

public class BoxRenderer extends AbstractPosRenderer {

    @Override
    protected void configureBuffers(int vao, int vbo) {
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);
    }

    @Override
    protected ShaderProgram makeShader() throws Exception {
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/assets/OpenGLHF/shaders/box.vert"));
        shaderProgram.createGeometryShader(Utils.loadResource("/assets/OpenGLHF/shaders/box.geom"));
        shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/box.frag"));
        shaderProgram.link();
        return shaderProgram;
    }

    protected DoubleStream toVertices(Entity entity, float tickDelta) {
        var lerpX = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - entity.getX();
        var lerpY = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - entity.getY();
        var lerpZ = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ();
        var box = entity.getBoundingBox().offset(lerpX, lerpY, lerpZ);

        return DoubleStream.of(
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ
        );
    }

    @Override
    protected void glDrawArrays(int length) {
        GL33.glDrawArrays(GL33.GL_LINES, 0, length / 3);
    }
}
