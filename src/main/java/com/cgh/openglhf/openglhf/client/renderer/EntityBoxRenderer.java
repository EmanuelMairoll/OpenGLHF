package com.cgh.openglhf.openglhf.client.renderer;

import com.cgh.openglhf.openglhf.client.ShaderProgram;
import com.cgh.openglhf.openglhf.client.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public class EntityBoxRenderer implements OpenGLHFRenderer {

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

    @Override
    public boolean isRenderingEnabled() {
        return boundingBoxRenderer.isRenderingEnabled();
    }

    @Override
    public void setRenderingEnabled(boolean renderingEnabled) {
        this.boundingBoxRenderer.setRenderingEnabled(renderingEnabled);
    }


    public void render(WorldRenderContext worldRenderContext) {
        this.boundingBoxRenderer.render(worldRenderContext);
    }

}
