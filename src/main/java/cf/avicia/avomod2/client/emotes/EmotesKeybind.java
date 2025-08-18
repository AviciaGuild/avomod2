package cf.avicia.avomod2.client.emotes;

import cf.avicia.avomod2.client.AvoMod2Client;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class EmotesKeybind {
    private static boolean isHeld = false;
    public static KeyBinding keyBinding;
    public static void init() {
        keyBinding = new KeyBinding("Open emotes menu", InputUtil.GLFW_KEY_V, "Avomod");
        KeyBindingRegistryImpl.registerKeyBinding(keyBinding);
    }

    public static void onTick() {
        if (keyBinding.isPressed()) {
            if (!isHeld) {
                isHeld = true;
                AvoMod2Client.screenToRender = new EmotesGui(keyBinding);
            }
        } else if (!(MinecraftClient.getInstance().currentScreen instanceof EmotesGui)) {
            isHeld = false;
        }
    }
}
