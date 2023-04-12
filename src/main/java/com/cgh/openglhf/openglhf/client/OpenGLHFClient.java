package com.cgh.openglhf.openglhf.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

public class OpenGLHFClient implements ClientModInitializer {
    private TriangleRenderer triangleRenderer;

    @Override
    public void onInitializeClient() {
        MinecraftClient.getInstance().execute(() -> {
            try {
                triangleRenderer = new TriangleRenderer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        WorldRenderEvents.AFTER_ENTITIES.register(this::renderAfterEntities);
    }

    private void renderAfterEntities(WorldRenderContext worldRenderContext) {
        triangleRenderer.render(worldRenderContext);
    }
}
