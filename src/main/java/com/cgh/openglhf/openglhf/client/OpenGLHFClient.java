package com.cgh.openglhf.openglhf.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.lwjgl.opengl.GL32;

public class OpenGLHFClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.BEFORE_ENTITIES.register(this::renderBeforeEntities);
    }

    private void renderBeforeEntities(WorldRenderContext worldRenderContext) {
        GL32.glPushMatrix();

        // render stuff here

        GL32.glPopMatrix();
    }
}
