package com.gojo.client;

import com.gojo.client.config.ConfigManager;
import com.gojo.client.module.Modules;
import com.gojo.client.ui.GojoScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public final class GojoClient implements ClientModInitializer {
    private static KeyMapping openMenu;
    @Override public void onInitializeClient() {
        Modules.init();
        ConfigManager.load();
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.gojoclient.menu", GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.consumeClick()) client.setScreen(new GojoScreen());
            Modules.tick(client);
        });
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::save));
        System.out.println("[GOJO CLIENT] Inicializado para Minecraft 1.21.11");
    }
}