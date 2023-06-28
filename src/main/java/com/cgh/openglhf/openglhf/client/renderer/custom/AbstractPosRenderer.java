package com.cgh.openglhf.openglhf.client.renderer.custom;

import com.cgh.openglhf.openglhf.client.ShaderProgram;
import com.cgh.openglhf.openglhf.client.Utils;
import com.cgh.openglhf.openglhf.client.renderer.OpenGLHFRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;

public abstract class AbstractPosRenderer implements OpenGLHFRenderer {

    private final int vao;
    private final int vbo;
    private ShaderProgram shaderProgram;
    private boolean renderingEnabled = false;

    public AbstractPosRenderer() {

        vao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(vao);

        vbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        configureBuffers(vao, vbo);

        try {
            this.shaderProgram = makeShader();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        GL33.glBindVertexArray(0);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
    }

    protected abstract void configureBuffers(final int vao, final int vbo);

    protected abstract ShaderProgram makeShader() throws Exception;

    protected abstract DoubleStream toVertices(Entity entity, float tickDelta);

    protected abstract void glDrawArrays(int length);

    protected void updateUniforms() {}

    public void render(WorldRenderContext worldRenderContext) {
        if(!renderingEnabled) return;

        var player = MinecraftClient.getInstance().player;
        if(player == null) return;

        var world = MinecraftClient.getInstance().world;
        if(world == null) return;

        var cameraPos = worldRenderContext.camera().getPos();
        var matrices = worldRenderContext.matrixStack();

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        shaderProgram.setModelViewMat(matrices.peek().getPositionMatrix());
        matrices.pop();
        shaderProgram.setProjectionMat(worldRenderContext.projectionMatrix());

        int prevVAO = GL33.glGetInteger(GL33.GL_VERTEX_ARRAY_BINDING);
        GL33.glBindVertexArray(vao);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        var vertices = StreamSupport.stream(world.getEntities().spliterator(), false)
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity != player)
                .flatMapToDouble(e -> toVertices(e, worldRenderContext.tickDelta()))
                .toArray();

        var vertexBufferData = MemoryUtil.memAllocFloat(vertices.length);
        vertexBufferData.put(Utils.doublesToFloat(vertices)).flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertexBufferData, GL33.GL_STATIC_DRAW);

        MemoryUtil.memFree(vertexBufferData);

        GL33.glDisable(GL33.GL_CULL_FACE);
        GL33.glDisable(GL33.GL_DEPTH_TEST);

        shaderProgram.bind();

        updateUniforms();

        glDrawArrays(vertices.length);

        shaderProgram.unbind();

        GL33.glEnable(GL33.GL_CULL_FACE);
        GL33.glEnable(GL33.GL_DEPTH_TEST);

        GL33.glBindVertexArray(prevVAO);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
    }

    @Override
    public boolean isRenderingEnabled() {
        return renderingEnabled;
    }

    @Override
    public void setRenderingEnabled(boolean renderingEnabled) {
        this.renderingEnabled = renderingEnabled;
    }
}
