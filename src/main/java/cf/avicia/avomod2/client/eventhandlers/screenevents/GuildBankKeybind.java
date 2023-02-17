package cf.avicia.avomod2.client.eventhandlers.screenevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.utils.Utils;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class GuildBankKeybind {
    private static boolean openingBank;
    private static KeyBinding keyBinding;

    private static long lastKeybindPress = System.currentTimeMillis();

    public static void init() {
        keyBinding = new KeyBinding("Keybind to open guild bank", InputUtil.GLFW_KEY_Y, "Avomod");
        KeyBindingRegistryImpl.registerKeyBinding(keyBinding);
    }

    public static void onTick() {
        if (MinecraftClient.getInstance().player == null) return;
        // Stop trying to open the bank after 2 seconds
        if (openingBank && System.currentTimeMillis() - lastKeybindPress > 2000) {
            openingBank = false;
        }
        if (keyBinding.isPressed()) {
            lastKeybindPress = System.currentTimeMillis();
            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                MinecraftClient.getInstance().getNetworkHandler().sendCommand("gu manage");
                openingBank = true;
            }
        }
    }

    public static void afterRender(MinecraftClient client, Screen screen, Screen screen1) {
        if (ConfigsHandler.getConfigBoolean("disableAll") || client.player == null || screen == null) return;
        if (!(screen1 instanceof GenericContainerScreen) || !openingBank || !screen1.getTitle().getString().contains(": Manage"))
            return;
        ScreenHandler screenHandler = client.player.currentScreenHandler;
        ItemStack guildBankItem = screenHandler.slots.get(15).getStack();
        if (!guildBankItem.getName().getString().contains("Bank")) return;
        Utils.sendClickPacket(
                screenHandler,
                15,
                0,
                SlotActionType.PICKUP,
                guildBankItem
        );
    }
}
