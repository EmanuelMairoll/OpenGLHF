package com.cgh.openglhf.openglhf.client;

import com.cgh.openglhf.openglhf.client.renderer.custom.BoxRenderer;
import com.cgh.openglhf.openglhf.client.renderer.custom.RectRenderer;
import com.cgh.openglhf.openglhf.client.renderer.custom.TraceRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class OpenGLHFClient implements ClientModInitializer {
    private TraceRenderer traceRenderer;
    private RectRenderer rectRenderer;
    private BoxRenderer boxRenderer;

    private static final KeyBinding POS_RENDERER_KEY_BINDING;
    private static final KeyBinding RECT_RENDERER_KEY_BINDING;
    private static final KeyBinding BOX_RENDERER_KEY_BINDING;

    static {
        POS_RENDERER_KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.openglhf.pos", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_F6, // The keycode of the key
                "category.openglhf.pos" // The translation key of the keybinding's category.
        ));

        RECT_RENDERER_KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.openglhf.rect", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_F7, // The keycode of the key
                "category.openglhf.rect" // The translation key of the keybinding's category.
        ));

        BOX_RENDERER_KEY_BINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.openglhf.box", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_F8, // The keycode of the key
                "category.openglhf.box" // The translation key of the keybinding's category.
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

        WorldRenderEvents.END.register(this::renderAfterEntities);
        ClientTickEvents.END_CLIENT_TICK.register(this::handleKeyBindings);

    }

    private void initRenderers() throws Exception {
        traceRenderer = new TraceRenderer();
        rectRenderer = new RectRenderer();
        boxRenderer = new BoxRenderer();
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
    }

    private void renderAfterEntities(WorldRenderContext worldRenderContext) {
        traceRenderer.render(worldRenderContext);
        rectRenderer.render(worldRenderContext);
        boxRenderer.render(worldRenderContext);
    }
}
