package com.cgh.openglhf.openglhf.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public class EntityBoxRenderer {

    private final EntityBoundingBoxRenderer boundingBoxRenderer;

    public EntityBoxRenderer() throws Exception {

        ShaderProgram shaderProgram;

        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/assets/OpenGLHF/shaders/box.vert"));
        shaderProgram.createGeometryShader(Utils.loadResource("/assets/OpenGLHF/shaders/box.geom"));
        shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/box.frag"));
        shaderProgram.link();

        this.boundingBoxRenderer = new EntityBoundingBoxRenderer(shaderProgram);

    }

    public void render(WorldRenderContext worldRenderContext) {
        this.boundingBoxRenderer.render(worldRenderContext);
    }

}
