package com.cgh.openglhf.openglhf.client.renderer.blaze3d;

import com.cgh.openglhf.openglhf.client.renderer.OpenGLHFRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import static net.minecraft.client.gui.DrawableHelper.drawTexture;

public class InfoPanelRenderer implements OpenGLHFRenderer {

    // Map of entity to pair of (lastScale, currentScale)
    private final Map<LivingEntity, Pair<Integer, Integer>> entityMap = new HashMap<>();
    private boolean renderingEnabled = false;

    public void updateAnimations() {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;

        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;

        Vec3d cameraPos = player.getCameraPosVec(1.0F);
        Vec3d rotation = player.getRotationVec(1.0F);
        Vec3d endPos = cameraPos.add(rotation.x * 32, rotation.y * 32, rotation.z * 32);

        var self = MinecraftClient.getInstance().player;
        StreamSupport.stream(
                        world.getEntities().spliterator(), false)
                .filter(entity -> entity instanceof LivingEntity)
                .map(entity -> (LivingEntity) entity)
                .filter(entity -> entity != self)
                .forEach(entity -> {
                    Box entityBox = entity.getBoundingBox().expand(0.5);
                    boolean lookingAt = entityBox.raycast(cameraPos, endPos).isPresent();

                    entityMap.compute(entity, (k, v) -> {
                        if (v == null) {
                            return lookingAt ? p(0, 1) : p(0, 0);
                        } else {
                            if (lookingAt) {
                                return p(v.getRight(), Math.min(v.getRight() + 1, 5));
                            } else {
                                return p(v.getRight(), Math.max(v.getRight() - 1, 0));
                            }
                        }
                    });
                });

        entityMap.keySet().removeIf(entity -> entity.isRemoved() || entity.isDead() || entity.getHealth() <= 0);
    }

    public static <A, B> Pair<A, B> p(A a, B b) {
        return new Pair<>(a, b);
    }

    @Override
    public boolean isRenderingEnabled() {
        return renderingEnabled;
    }

    @Override
    public void setRenderingEnabled(boolean enabled) {
        this.renderingEnabled = enabled;
    }

    private static float bezierBlend(float t) {
        return t * t * (3.0f - 2.0f * t);
    }

    @Override
    public void render(WorldRenderContext worldRenderContext) {
        if (!renderingEnabled) return;

        var dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        var matrices = worldRenderContext.matrixStack();
        var tickDelta = worldRenderContext.tickDelta();

        for (var entry : entityMap.entrySet()) {
            var entity = entry.getKey();
            var lcScale = entry.getValue();

            if (lcScale.getLeft() == 0 && lcScale.getRight() == 0) {
                continue;
            }

            var scale = bezierBlend(MathHelper.lerp(tickDelta, (float) lcScale.getLeft(), (float) lcScale.getRight()) / 5.0F);
            System.out.println(entity.getName().getString() + " " + lcScale.getLeft() + " " + lcScale.getRight() + " " + scale);

            double lerpedEntityPosX = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
            double lerpedEntityPosY = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
            double lerpedEntityPosZ = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());

            Vec3d entityOffset = dispatcher.getRenderer(entity).getPositionOffset(entity, tickDelta);
            Vec3d cameraPos = worldRenderContext.camera().getPos();
            double dX = lerpedEntityPosX - cameraPos.x + entityOffset.getX();
            double dY = lerpedEntityPosY - cameraPos.y + entityOffset.getY();
            double dZ = lerpedEntityPosZ - cameraPos.z + entityOffset.getZ();

            matrices.push();
            matrices.translate(dX, dY, dZ);

            int light = dispatcher.getLight(entity, tickDelta);
            var vertexConsumers = worldRenderContext.consumers();

            if (vertexConsumers == null) {
                return;
            }

            float f = entity.getHeight() / 2;
            matrices.translate(0.0F, f, 0.0F);
            matrices.multiply(dispatcher.getRotation());
            matrices.scale(-0.025F, -0.025F, 0.025F);
            matrices.scale(scale, scale, scale);
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();

            var dim = backplateDimensions(entity.getWidth(), entity.getHeight());

            renderBackplate(dim, vertexConsumers, matrix4f, light);

            renderText(entity.getDisplayName(), 0, dim, vertexConsumers, matrix4f, light);
            RenderSystem.enableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            var d = entity.squaredDistanceTo(MinecraftClient.getInstance().player);
            int distance = (int) Math.sqrt(d);
            renderText(Text.of(distance + "m"), 1, dim, vertexConsumers, matrix4f, light);

            renderHearts(entity, dim, matrices);
            renderArmor(entity, dim, matrices);

            matrices.pop();
        }
    }

    private static Rect2i backplateDimensions(float entityWidth, float entityHeight) {
        var bbMax = (int) (Math.max(entityWidth, entityHeight) / 0.025f);

        var x = -((int) entityWidth / 2) - 50;
        var width = (int) entityWidth + 50 + 50;

        var y = -(bbMax / 2) - 40;
        var height = bbMax + 40;

        return new Rect2i(x, y, width, height);
    }

    private static void renderBackplate(Rect2i dim, VertexConsumerProvider vertexConsumers, Matrix4f matrix4f, int light) {
        float rectAlpha = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getTextBackground());

        var rectMinX = dim.getX();
        var rectMaxX = dim.getX() + dim.getWidth();
        var rectMinY = dim.getY();
        var rectMaxY = dim.getY() + dim.getHeight();

        vertexConsumer.vertex(matrix4f, rectMinX, rectMaxY, 0.01f).color(0, 0, 0, rectAlpha).light(light).next();
        vertexConsumer.vertex(matrix4f, rectMaxX, rectMaxY, 0.01f).color(0, 0, 0, rectAlpha).light(light).next();
        vertexConsumer.vertex(matrix4f, rectMaxX, rectMinY, 0.01f).color(0, 0, 0, rectAlpha).light(light).next();
        vertexConsumer.vertex(matrix4f, rectMinX, rectMinY, 0.01f).color(0, 0, 0, rectAlpha).light(light).next();
    }

    private static void renderText(Text text, int line, Rect2i dim, VertexConsumerProvider vertexConsumers, Matrix4f matrix4f, int light) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        float x = (float) textRenderer.getWidth(text) / 2;
        MinecraftClient.getInstance().textRenderer.draw(text, -x, dim.getY() + 3 + (line * 10), -1, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
    }

    private void renderHearts(LivingEntity entity, Rect2i dim, MatrixStack matrices) {
        RenderSystem.setShaderTexture(0, InGameHud.GUI_ICONS_TEXTURE);

        float maxHealth = entity.getMaxHealth();
        float health = entity.getHealth();

        int fullHearts = (int) (health / 2);
        int halfHearts = (int) (health % 2);
        int containers = (int) (maxHealth / 2);

        if (containers == 0) {
            return;
        }

        int x = dim.getX() + 3;
        int startY = dim.getY() + 3;
        int lineHeight = Math.min(10, (dim.getHeight() - 6) / containers);

        for (int i = 0; i < containers; i++) {
            int y = startY + (i * lineHeight);
            float z = -0.001f - (i * 0.001f);

            drawHeart(matrices, HeartType.CONTAINER, x, y, z, false, false);

            if (i < fullHearts) {
                // draw a full heart
                drawHeart(matrices, HeartType.NORMAL, x, y, z, false, false);
            } else if (i < fullHearts + halfHearts) {
                // draw a half heart
                drawHeart(matrices, HeartType.NORMAL, x, y, z, false, true);
            }
        }
    }

    private void drawHeart(MatrixStack matrices, HeartType type, int x, int y, float z, boolean blinking, boolean halfHeart) {
        RenderSystem.enableDepthTest();
        drawTexture(matrices, x, y, z, (float) type.getU(halfHeart, blinking), (float) type.getV(), 9, 9, 256, 256);
        RenderSystem.disableDepthTest();
    }

    private enum HeartType {
        CONTAINER(0, false),
        NORMAL(2, true);
        private final int textureIndex;
        private final boolean hasBlinkingTexture;

        private HeartType(int textureIndex, boolean hasBlinkingTexture) {
            this.textureIndex = textureIndex;
            this.hasBlinkingTexture = hasBlinkingTexture;
        }

        public int getU(boolean halfHeart, boolean blinking) {
            int i;
            if (this == CONTAINER) {
                i = blinking ? 1 : 0;
            } else {
                int j = halfHeart ? 1 : 0;
                int k = this.hasBlinkingTexture && blinking ? 2 : 0;
                i = j + k;
            }

            return 16 + (this.textureIndex * 2 + i) * 9;
        }

        public int getV() {
            return 0;
        }
    }

    private void renderArmor(LivingEntity entity, Rect2i dim, MatrixStack matrices) {
        RenderSystem.setShaderTexture(0, InGameHud.GUI_ICONS_TEXTURE);

        int armorValue = entity.getArmor();
        int fullArmorIcons = armorValue / 2;
        int halfArmorIcons = armorValue % 2;
        int sumArmorIcons = fullArmorIcons + halfArmorIcons;

        if (sumArmorIcons == 0) {
            return;
        }

        int x = dim.getX() + dim.getWidth() - 9 - 3;
        int startY = dim.getY() + 3;
        int lineHeight = Math.min(10, (dim.getHeight() - 3) / sumArmorIcons);

        for (int i = 0; i < sumArmorIcons; i++) {
            int y = startY + (i * lineHeight);
            float z = -0.001f - (i * 0.001f);

            if (i < fullArmorIcons) {
                // draw a full armor icon
                drawArmorIcon(matrices, x, y, z, false);
            } else if (i < fullArmorIcons + halfArmorIcons) {
                // draw a half armor icon
                drawArmorIcon(matrices, x, y, z, true);
            }
        }
    }

    private void drawArmorIcon(MatrixStack matrices, int x, int y, float z, boolean halfIcon) {
        RenderSystem.enableDepthTest();
        drawTexture(matrices, x, y, z, halfIcon ? 25 : 34, 9, 9, 9, 256, 256);
        RenderSystem.disableDepthTest();
    }


    public static void drawTexture(MatrixStack matrices, int x, int y, float z, float u, float v, int width, int height, int textureWidth, int textureHeight) {
        int xMax = x + width;
        int yMax = y + height;

        float uMin = u / (float) textureWidth;
        float uMax = (u + (float) width) / (float) textureWidth;

        float vMin = (v + 0.0F) / (float) textureHeight;
        float vMax = (v + (float) height) / (float) textureHeight;

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, (float) x, (float) y, z).texture(uMin, vMin).next();
        bufferBuilder.vertex(matrix, (float) x, (float) yMax, z).texture(uMin, vMax).next();
        bufferBuilder.vertex(matrix, (float) xMax, (float) yMax, z).texture(uMax, vMax).next();
        bufferBuilder.vertex(matrix, (float) xMax, (float) y, z).texture(uMax, vMin).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }
}

