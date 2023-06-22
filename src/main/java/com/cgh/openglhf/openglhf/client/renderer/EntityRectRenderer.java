package com.cgh.openglhf.openglhf.client.renderer;

import com.cgh.openglhf.openglhf.client.ShaderProgram;
import com.cgh.openglhf.openglhf.client.Utils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public class EntityRectRenderer implements OpenGLHFRenderer {

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
