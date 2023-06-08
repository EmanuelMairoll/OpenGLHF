package com.cgh.openglhf.openglhf.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.stream.StreamSupport;

public class OpenGLHFClient implements ClientModInitializer {
    private EntityPosRenderer entityPosRenderer;

    @Override
    public void onInitializeClient() {
        MinecraftClient.getInstance().execute(() -> {
            try {
                entityPosRenderer = new EntityPosRenderer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        WorldRenderEvents.BEFORE_ENTITIES.register(this::renderBeforeEntities);
        WorldRenderEvents.AFTER_ENTITIES.register(this::renderAfterEntities);

    }

    private void renderBeforeEntities(WorldRenderContext worldRenderContext) {
        entityPosRenderer.render(worldRenderContext);
    }

    private void renderAfterEntities(WorldRenderContext worldRenderContext) {
        var entities = StreamSupport.stream(
                        MinecraftClient.getInstance().world.getEntities().spliterator(), false)
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .toList();

        var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        var matrices = worldRenderContext.matrixStack();
        var tickDelta = worldRenderContext.tickDelta();
        for (LivingEntity entity : entities) {

            // from WorldRenderer.renderEntity
            double lerpedEntityX = MathHelper.lerp((double) tickDelta, entity.lastRenderX, entity.getX());
            double lerpedEntityY = MathHelper.lerp((double) tickDelta, entity.lastRenderY, entity.getY());
            double lerpedEntityZ = MathHelper.lerp((double) tickDelta, entity.lastRenderZ, entity.getZ());
            var cameraPos = worldRenderContext.camera().getPos();

            // from EntityRenderDispatcher.render
            Vec3d entityOffset = dispatcher.getRenderer(entity).getPositionOffset(entity, tickDelta);


            double dX = lerpedEntityX - cameraPos.x + entityOffset.getX();
            double dY = lerpedEntityY - cameraPos.y + entityOffset.getY();
            double dZ = lerpedEntityZ - cameraPos.z + entityOffset.getZ();

            matrices.push();
            matrices.translate(dX, dY, dZ);

            //from EntityRenderer.render
            renderLabelIfPresent(entity, Text.of("Test"), worldRenderContext.matrixStack(), worldRenderContext.consumers(), tickDelta);

            matrices.pop();
        }
    }


    protected void renderLabelIfPresent(LivingEntity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta) {
        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int light = dispatcher.getLight(entity, tickDelta);

        double d = dispatcher.getSquaredDistanceToCamera(entity);
        if (!(d > 4096.0)) {
            boolean bl = !entity.isSneaky();
            float f = entity.getHeight() + 0.5F;
            int i = "deadmau5".equals(text.getString()) ? -10 : 0;
            matrices.push();
            matrices.translate(0.0F, f, 0.0F);
            matrices.multiply(dispatcher.getRotation());
            matrices.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int j = (int) (g * 255.0F) << 24;
            float h = (float) (-textRenderer.getWidth((StringVisitable) text) / 2);
            textRenderer.draw(text, h, (float) i, 553648127, false, matrix4f, vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, light);
            if (bl) {
                textRenderer.draw((Text) text, h, (float) i, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
            }

            matrices.pop();
        }
    }
}
