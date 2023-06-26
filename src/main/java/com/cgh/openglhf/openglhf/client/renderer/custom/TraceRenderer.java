package com.cgh.openglhf.openglhf.client.renderer.custom;

import com.cgh.openglhf.openglhf.client.ShaderProgram;
import com.cgh.openglhf.openglhf.client.Utils;
import net.minecraft.client.MinecraftClient;
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
        shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.frag"));
        shaderProgram.link();
        return shaderProgram;
    }

    @Override
    protected DoubleStream toVertices(Entity entity, float tickDelta) {
        var player = MinecraftClient.getInstance().player;
        if (player == null) {
            return DoubleStream.of();
        }

        var lerpedEntityX = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
        var lerpedEntityY = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
        var lerpedEntityZ = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());


        var lerpedPlayerX = MathHelper.lerp(tickDelta, player.lastRenderX, player.getX());
        var lerpedPlayerY = MathHelper.lerp(tickDelta, player.lastRenderY, player.getY());
        var lerpedPlayerZ = MathHelper.lerp(tickDelta, player.lastRenderZ, player.getZ());

        var crosshairX = lerpedPlayerX + player.getRotationVector().x;
        var crosshairY = lerpedPlayerY + player.getEyeHeight(player.getPose()) + player.getRotationVector().y;
        var crosshairZ = lerpedPlayerZ + player.getRotationVector().z;

        return DoubleStream.of(
                lerpedEntityX, lerpedEntityY, lerpedEntityZ,
                crosshairX, crosshairY, crosshairZ
        );

    }

    protected void glDrawArrays(int length) {
        GL33.glDrawArrays(GL33.GL_LINES, 0, length);
    }
}
