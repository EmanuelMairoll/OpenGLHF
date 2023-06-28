package com.cgh.openglhf.openglhf.client;

import com.cgh.openglhf.openglhf.client.renderer.blaze3d.InfoPanelRenderer;
import com.cgh.openglhf.openglhf.client.renderer.custom.BoxRenderer;
import com.cgh.openglhf.openglhf.client.renderer.custom.EllipseRenderer;
import com.cgh.openglhf.openglhf.client.renderer.custom.TraceRenderer;
import com.cgh.openglhf.openglhf.client.renderer.custom.RectRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class OpenGLHFClient implements ClientModInitializer {
    private TraceRenderer traceRenderer;
    private RectRenderer rectRenderer;
    private BoxRenderer boxRenderer;
    private InfoPanelRenderer infoPanelRenderer;
    private EllipseRenderer ellipseRenderer;

    private static final KeyBinding POS_RENDERER_KEY_BINDING;
    private static final KeyBinding RECT_RENDERER_KEY_BINDING;
    private static final KeyBinding BOX_RENDERER_KEY_BINDING;
    private static final KeyBinding PANEL_RENDERER_KEY_BINDING;
    private static final KeyBinding ELLIPSE_RENDERER_KEY_BINDING;
    private static final KeyBinding TRACER_ELLIPSE_TEST_KEY_BINDING;

    static {
        POS_RENDERER_KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.openglhf.pos", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_F6, // The keycode of the key
                "category.openglhf.pos" // The translation key of the keybinding's category.
        ));

        RECT_RENDERER_KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.openglhf.rect",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.openglhf.rect"
        ));

        BOX_RENDERER_KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.openglhf.box",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                "category.openglhf.box"
        ));

        PANEL_RENDERER_KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.openglhf.panel",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.openglhf.panel"
        ));

        ELLIPSE_RENDERER_KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.openglhf.ellipse",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F10,
                "category.openglhf.ellipse"
        ));

        TRACER_ELLIPSE_TEST_KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.openglhf.ellipse_test",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F12,
                "category.openglhf.ellipse_test"
        ));

    }

    @Override
    public void onInitializeClient() {
        MinecraftClient.getInstance().execute(() -> {
            try {
                this.initRenderers();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        WorldRenderEvents.AFTER_ENTITIES.register(this::renderAfterEntities);
        ClientTickEvents.END_WORLD_TICK.register(this::updateAnimations);
        ClientTickEvents.END_CLIENT_TICK.register(this::handleKeyBindings);

    }

    private void initRenderers() throws Exception {
        traceRenderer = new TraceRenderer();
        traceRenderer.setEllipseTestMajorAxis(0.4f);
        traceRenderer.setEllipseTestMinorAxis(0.4f);
        ellipseRenderer = new EllipseRenderer(0.4f, 0.4f);

        rectRenderer = new RectRenderer();
        boxRenderer = new BoxRenderer();
        infoPanelRenderer = new InfoPanelRenderer();
    }

    private void handleKeyBindings(MinecraftClient client) {

        if(client.player == null) return;

        if (POS_RENDERER_KEY_BINDING.wasPressed()) {
            traceRenderer.setRenderingEnabled(!traceRenderer.isRenderingEnabled());
            client.player.sendMessage(Text.literal(String.format("TRACE renderer: %b", traceRenderer.isRenderingEnabled())), false);
        }

        if (RECT_RENDERER_KEY_BINDING.wasPressed()) {
            rectRenderer.setRenderingEnabled(!rectRenderer.isRenderingEnabled());
            client.player.sendMessage(Text.literal(String.format("RECT renderer: %b", rectRenderer.isRenderingEnabled())), false);
        }

        if (BOX_RENDERER_KEY_BINDING.wasPressed()) {
            boxRenderer.setRenderingEnabled(!boxRenderer.isRenderingEnabled());
            client.player.sendMessage(Text.literal(String.format("BOX renderer: %b", boxRenderer.isRenderingEnabled())), false);
        }

        if (PANEL_RENDERER_KEY_BINDING.wasPressed()) {
            infoPanelRenderer.setRenderingEnabled(!infoPanelRenderer.isRenderingEnabled());
            client.player.sendMessage(Text.literal(String.format("PANEL renderer: %b", infoPanelRenderer.isRenderingEnabled())), false);
        }

        if (ELLIPSE_RENDERER_KEY_BINDING.wasPressed()) {
            ellipseRenderer.setRenderingEnabled(!ellipseRenderer.isRenderingEnabled());
            client.player.sendMessage(Text.literal(String.format("ELLIPSE renderer: %b", ellipseRenderer.isRenderingEnabled())), false);
        }

        if (TRACER_ELLIPSE_TEST_KEY_BINDING.wasPressed()) {
            traceRenderer.setEllipseTestEnabled(!traceRenderer.isEllipseTestEnabled());
            client.player.sendMessage(Text.literal(String.format("TRACER ellipse test: %b", traceRenderer.isEllipseTestEnabled())), false);
        }
    }

    private void renderAfterEntities(WorldRenderContext worldRenderContext) {
        traceRenderer.render(worldRenderContext);
        rectRenderer.render(worldRenderContext);
        boxRenderer.render(worldRenderContext);
        infoPanelRenderer.render(worldRenderContext);
        ellipseRenderer.render(worldRenderContext);
    }

    private void updateAnimations(ClientWorld clientWorld) {
        infoPanelRenderer.updateAnimations();
    }

}
