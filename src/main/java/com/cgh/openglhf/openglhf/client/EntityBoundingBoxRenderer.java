package com.cgh.openglhf.openglhf.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;

public class EntityBoundingBoxRenderer {

    private final int vao;
    private final int vbo;
    private ShaderProgram shaderProgram;

    public EntityBoundingBoxRenderer(ShaderProgram shaderProgram) throws Exception {

        vao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(vao);

        vbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);

        this.shaderProgram = shaderProgram;

        unbindBuffers();
    }

    public void render(WorldRenderContext worldRenderContext) {
        var cameraPos = worldRenderContext.camera().getPos();
        var matrices = worldRenderContext.matrixStack();

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        shaderProgram.setModelViewMat(matrices.peek().getPositionMatrix());
        matrices.pop();
        shaderProgram.setProjectionMat(worldRenderContext.projectionMatrix());

        GL33.glBindVertexArray(vao);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        var entities = MinecraftClient.getInstance().world.getEntities();

        var vertices = StreamSupport.stream(entities.spliterator(), false)
                .filter(e -> e instanceof SheepEntity)
                .flatMapToDouble(e -> boxMinMaxToVertices(e, worldRenderContext.tickDelta()))
                .toArray();

        var vertexBufferData = MemoryUtil.memAllocFloat(vertices.length);
        vertexBufferData.put(Utils.doublesToFloat(vertices)).flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertexBufferData, GL33.GL_STATIC_DRAW);

        MemoryUtil.memFree(vertexBufferData);

        GL33.glDisable(GL33.GL_CULL_FACE);
        GL33.glDisable(GL33.GL_DEPTH_TEST);

        shaderProgram.bind();

        GL33.glDrawArrays(GL33.GL_LINES, 0, vertices.length / 3);

        shaderProgram.unbind();

        GL33.glEnable(GL33.GL_CULL_FACE);
        GL33.glEnable(GL33.GL_DEPTH_TEST);

        unbindBuffers();
    }


    private void unbindBuffers() {
        GL33.glBindVertexArray(0);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
    }

    private DoubleStream boxMinMaxToVertices(Entity entity, float tickDelta) {
        var lerpX = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()) - entity.getX();
        var lerpY = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()) - entity.getY();
        var lerpZ = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ();
        var box = entity.getBoundingBox().offset(lerpX, lerpY, lerpZ);

        return DoubleStream.of(
                box.minX, box.minY, box.minZ,
                box.maxX, box.maxY, box.maxZ
        );
    }
}
