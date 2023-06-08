package com.cgh.openglhf.openglhf.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.Iterator;
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
                .filter(entity -> !(entity instanceof PlayerEntity))
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
            float f = entity.getHeight() / 2;
            int y = "deadmau5".equals(text.getString()) ? -10 : 0;
            matrices.push();
            matrices.translate(0.0F, f, 0.0F);
            matrices.multiply(dispatcher.getRotation());
            matrices.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            float rectAlpha = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            float x = (float) (-textRenderer.getWidth((StringVisitable) text) / 2);

            textRenderer.draw((Text) text, x, (float) y - 45, -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

            var rectMinX = -30;
            var rectMinY = 30;
            var rectMaxX = 31;
            var rectMaxY = -46;
            var rectZ = 0.01f;
            var rectRed = 0;
            var rectGreen = 0;
            var rectBlue = 0;

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTextBackground());
            vertexConsumer.vertex(matrix4f, rectMinX, rectMinY, rectZ).color(rectRed, rectGreen, rectBlue, rectAlpha).light(light).next();
            vertexConsumer.vertex(matrix4f, rectMaxX, rectMinY, rectZ).color(rectRed, rectGreen, rectBlue, rectAlpha).light(light).next();
            vertexConsumer.vertex(matrix4f, rectMaxX, rectMaxY, rectZ).color(rectRed, rectGreen, rectBlue, rectAlpha).light(light).next();
            vertexConsumer.vertex(matrix4f, rectMinX, rectMaxY, rectZ).color(rectRed, rectGreen, rectBlue, rectAlpha).light(light).next();

            matrices.pop();
        }
    }
}
