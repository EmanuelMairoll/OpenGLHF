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

public class EntityPosRenderer implements OpenGLHFRenderer {

    private final int vao;
    private final int vbo;
    private final ShaderProgram shaderProgram;
    private boolean renderingEnabled = false;

    public EntityPosRenderer() throws Exception {

        vao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(vao);

        vbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);

        unbindBuffers();

        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.vert"));
        shaderProgram.createGeometryShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.geom"));
        shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.frag"));
        shaderProgram.link();
    }

    public void render(WorldRenderContext worldRenderContext) {
        if(!renderingEnabled) return;

        var pos = worldRenderContext.camera().getPos();
        var matrices = worldRenderContext.matrixStack();
        matrices.push();
        matrices.translate(-pos.x, -pos.y, -pos.z);
        shaderProgram.setModelViewMat(matrices.peek().getPositionMatrix());
        matrices.pop();

        shaderProgram.setProjectionMat(worldRenderContext.projectionMatrix());

        GL33.glBindVertexArray(vao);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        var tickDelta = worldRenderContext.tickDelta();
        var vertices = StreamSupport.stream(
                        MinecraftClient.getInstance().world.getEntities().spliterator(), false)
                .flatMapToDouble(e -> mapVertices(e, tickDelta))
                .toArray();
        var vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(Utils.doublesToFloat(vertices)).flip();
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertexBuffer, GL33.GL_STATIC_DRAW);
        MemoryUtil.memFree(vertexBuffer);


        shaderProgram.bind();
        GL33.glPointSize(10);
        GL33.glDrawArrays(GL33.GL_POINTS, 0, vertices.length / 3);
        shaderProgram.unbind();

        unbindBuffers();
    }

    @Override
    public boolean isRenderingEnabled() {
        return renderingEnabled;
    }

    @Override
    public void setRenderingEnabled(boolean renderingEnabled) {
        this.renderingEnabled = renderingEnabled;
    }

    private DoubleStream mapVertices(Entity entity, float tickDelta) {
        if (entity instanceof SheepEntity) {
           return DoubleStream.of(
                    MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()),
                    MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()),
                    MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()));
        } else {
            return DoubleStream.of();
        }
    }

    private void unbindBuffers() {
        GL33.glBindVertexArray(0);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
    }

    public void cleanup() {
        GL33.glDisableVertexAttribArray(0);
        unbindBuffers();
        GL33.glDeleteBuffers(vbo);
        GL33.glDeleteVertexArrays(vao);
        shaderProgram.cleanup();
    }

}
