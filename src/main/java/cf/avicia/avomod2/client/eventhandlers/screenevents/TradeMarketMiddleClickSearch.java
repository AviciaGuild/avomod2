package cf.avicia.avomod2.client.eventhandlers.screenevents;

import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;

public class TradeMarketMiddleClickSearch {

    private static boolean executing = false;

    public static ActionResult mouseClicked(double mouseX, double mouseY, int button, Slot clickedSlot, ScreenHandler screenHandler) {
        if (MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().currentScreen == null ||
                !MinecraftClient.getInstance().currentScreen.getTitle().getString().equals("\uDAFF\uDFE8\uE011") ||
                MinecraftClient.getInstance().getNetworkHandler() == null ||
                button != 2 || clickedSlot == null) {
            return ActionResult.SUCCESS;
        }
        String name = Utils.getUnformattedString(clickedSlot.getStack().getName().getString());
        if (name.equals("Air")) return ActionResult.SUCCESS;

        ItemStack searchItem = screenHandler.slots.get(47).getStack();

        if (!searchItem.getName().getString().contains("Search and Filter") || executing) return ActionResult.SUCCESS;
        executing = true;

        new Thread(() -> {
            Utils.sendClickPacket(
                    screenHandler,
                    47,
                    0,
                    SlotActionType.PICKUP,
                    searchItem
            );

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(name.replaceAll(" \\[.*]|[^A-Za-z0-9 \\-']", ""));

            executing = false;
        }).start();

        // Cancels the regular middle click
        return ActionResult.FAIL;
    }
}
