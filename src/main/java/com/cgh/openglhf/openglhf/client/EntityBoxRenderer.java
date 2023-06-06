package com.cgh.openglhf.openglhf.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;

import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;

public class EntityBoxRenderer {

    private final int vao;
    private final int vbo;
    private final ShaderProgram shaderProgram;

    public EntityBoxRenderer() throws Exception {

        vao = GL33.glGenVertexArrays();
        GL33.glBindVertexArray(vao);

        vbo = GL33.glGenBuffers();
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, vbo);

        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);

        GL33.glVertexAttribPointer(1, 3, GL33.GL_FLOAT, false, 0, 12);
        GL33.glEnableVertexAttribArray(1);

        unbindBuffers();

        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/assets/OpenGLHF/shaders/box.vert"));
        shaderProgram.createGeometryShader(Utils.loadResource("/assets/OpenGLHF/shaders/box.geom"));
        shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/tracers.frag"));
        shaderProgram.link();
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
                .flatMapToDouble(e -> describeBoxAsFloats(e.getBoundingBox()))
                .toArray();

        // override calculated vertices with
        // fixed, test NDC vertices
        // expectation: draw a rectangle in the players view
        /*
        vertices = new double[] {
                0.0, 0.0, 0.0,
                -0.5, 0.0, 0.0,
                -0.5, 0.5, 0.0
        };
        */

        var vertexBufferData = MemoryUtil.memAllocFloat(vertices.length);
        vertexBufferData.put(doublesToFloat(vertices)).flip();

        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, vertexBufferData, GL33.GL_STATIC_DRAW);

        MemoryUtil.memFree(vertexBufferData);

       // GL33.glDisable(GL33.GL_CULL_FACE);

        shaderProgram.bind();

        GL33.glDrawArrays(GL33.GL_POINTS, 0, vertices.length / 6);

        shaderProgram.unbind();

       // GL33.glEnable(GL33.GL_CULL_FACE);

        unbindBuffers();
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

    private float[] doublesToFloat(double[] array) {
        float[] inFloatForm = new float[array.length];
        for (int i = 0; i < array.length; i++)
            inFloatForm[i] = (float) array[i];
        return inFloatForm;
    }

    private DoubleStream describeBoxAsFloats(Box box) {
        return DoubleStream.of(
                box.maxX, box.maxY, box.maxZ,
                box.minX, box.minY, box.minZ
        );
    }

    private DoubleStream calculateVerticesFromBox(Box box) {

        // for now, get vertices that make up "front" facing rectangle
        // of the bounding box
        //
        //       ________
        //      /       /|
        //     /_______/ |  ___ upper right
        //     |      |  |
        //     |      |  |
        //     |      | /
        //     |______|/
        //     ^   ^------ "front" facing rectangle
        //     |
        //      ---------- lower left
        //

        return DoubleStream.of(
                box.minX, box.minY, box.maxZ, // lower left
                box.minX, box.maxY, box.maxZ, // upper left
                box.maxX, box.maxY, box.maxZ, // upper right
                box.maxX, box.minY, box.maxZ // lower right
        );

    }
}
