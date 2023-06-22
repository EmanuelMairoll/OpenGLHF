package com.cgh.openglhf.openglhf.client.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public interface OpenGLHFRenderer {

    boolean isRenderingEnabled();
    void setRenderingEnabled(boolean enabled);
    void render(WorldRenderContext worldRenderContext);

}
