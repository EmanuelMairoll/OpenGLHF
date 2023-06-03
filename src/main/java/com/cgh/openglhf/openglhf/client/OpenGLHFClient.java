package com.cgh.openglhf.openglhf.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

public class OpenGLHFClient implements ClientModInitializer {
    private EntityPosRenderer entityPosRenderer;
    private EntityBoxRenderer entityBoxRenderer;

    @Override
    public void onInitializeClient() {
        MinecraftClient.getInstance().execute(() -> {
            try {
                entityPosRenderer = new EntityPosRenderer();
                entityBoxRenderer = new EntityBoxRenderer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        WorldRenderEvents.BEFORE_ENTITIES.register(this::renderAfterEntities);
    }

    private void renderAfterEntities(WorldRenderContext worldRenderContext) {
        entityPosRenderer.render(worldRenderContext);
        entityBoxRenderer.render(worldRenderContext);
    }
}
