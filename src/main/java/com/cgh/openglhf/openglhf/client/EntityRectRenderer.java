package com.cgh.openglhf.openglhf.client;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public class EntityRectRenderer {

    private final EntityBoundingBoxRenderer boundingBoxRenderer;

    public EntityRectRenderer() throws Exception {

        ShaderProgram shaderProgram;

        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/assets/OpenGLHF/shaders/rect.vert"));
        shaderProgram.createGeometryShader(Utils.loadResource("/assets/OpenGLHF/shaders/rect.geom"));
        shaderProgram.createFragmentShader(Utils.loadResource("/assets/OpenGLHF/shaders/rect.frag"));
        shaderProgram.link();

        this.boundingBoxRenderer = new EntityBoundingBoxRenderer(shaderProgram);

    }

    public void render(WorldRenderContext worldRenderContext) {
        this.boundingBoxRenderer.render(worldRenderContext);
    }

}
