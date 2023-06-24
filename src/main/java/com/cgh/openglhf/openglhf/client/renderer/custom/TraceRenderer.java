package com.cgh.openglhf.openglhf.client.renderer.custom;

import com.cgh.openglhf.openglhf.client.ShaderProgram;
import com.cgh.openglhf.openglhf.client.Utils;
import com.cgh.openglhf.openglhf.client.renderer.OpenGLHFRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;

public class TraceRenderer implements OpenGLHFRenderer {

    private ShaderProgram.GLUniform distance;

    private final int vao;
    private final int vbo;
    private ShaderProgram shaderProgram;
    private boolean renderingEnabled = false;

    public TraceRenderer() {

        vao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(vao);

        vbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 16, 0);
        GL33.glEnableVertexAttribArray(0);

        GL33.glVertexAttribPointer(1, 1, GL33.GL_FLOAT, false, 16, 12);
        GL33.glEnableVertexAttribArray(1);

        try {
            this.shaderProgram = makeShader();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        GL33.glBindVertexArray(0);
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
    }

    protected ShaderProgram makeShader() throws Exception {
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.vert"));
        shaderProgram.createGeometryShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.geom"));
        shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.frag"));
        shaderProgram.link();

        this.distance = shaderProgram.createGLUniformIfExists("distance");

        return shaderProgram;
    }


    protected DoubleStream toVertices(Entity entity, float tickDelta) {

        var distance = entity.distanceTo(MinecraftClient.getInstance().player);

        if (entity instanceof SheepEntity) {
            return DoubleStream.of(
                    MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()),
                    MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()),
                    MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()),
                    distance);
        } else {
            return DoubleStream.of();
        }
    }

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

        glDrawArrays(vertices.length / 4);

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

    protected void glDrawArrays(int length) {
        GL33.glDrawArrays(GL33.GL_POINTS, 0, length);
    }
}
