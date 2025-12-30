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
import net.minecraft.util.Identifier;

public class TerritoryMenuKeybind {
    private static boolean openingTerritoryMenu;
    private static KeyBinding keyBinding;

    private static long lastKeybindPress = System.currentTimeMillis();

    public static void init() {
        keyBinding = new KeyBinding("Keybind to open territory menu", InputUtil.GLFW_KEY_I, new KeyBinding.Category(Identifier.of("avomod")));
        KeyBindingRegistryImpl.registerKeyBinding(keyBinding);
    }

    public static void onTick() {
        if (MinecraftClient.getInstance().player == null) return;
        // Stop trying to open the territory menu after 2 seconds
        if (openingTerritoryMenu && System.currentTimeMillis() - lastKeybindPress > 2000) {
            openingTerritoryMenu = false;
        }
        if (keyBinding.isPressed()) {
            lastKeybindPress = System.currentTimeMillis();
            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                MinecraftClient.getInstance().getNetworkHandler().sendChatCommand("gu manage");
                openingTerritoryMenu = true;
            }
        }
    }

    public static void afterRender(MinecraftClient client, Screen screen, Screen screen1) {
        if (ConfigsHandler.getConfigBoolean("disableAll") || client.player == null || screen == null) return;
        if (!(screen1 instanceof GenericContainerScreen) || !openingTerritoryMenu || !screen1.getTitle().getString().contains(": Manage"))
            return;
        ScreenHandler screenHandler = client.player.currentScreenHandler;
        ItemStack territoryMenuItem = screenHandler.slots.get(14).getStack();
        if (!territoryMenuItem.getName().getString().contains("Territories")) return;
        Utils.sendClickPacket(
                screenHandler,
                (short) 14,
                (byte) 0,
                SlotActionType.PICKUP,
                territoryMenuItem
        );
        openingTerritoryMenu = false;
    }
}
