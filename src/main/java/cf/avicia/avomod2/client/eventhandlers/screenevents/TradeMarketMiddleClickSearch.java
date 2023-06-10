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
                !MinecraftClient.getInstance().currentScreen.getTitle().getString().equals("Trade Market") ||
                MinecraftClient.getInstance().getNetworkHandler() == null ||
                button != 2 || clickedSlot == null) {
            return ActionResult.SUCCESS;
        }
        String name = Utils.getUnformattedString(clickedSlot.getStack().getName().getString());
        if (name.equals("Air")) return ActionResult.SUCCESS;

        ItemStack compass = screenHandler.slots.get(35).getStack();

        if (!compass.getName().getString().contains("Search Item") || executing) return ActionResult.SUCCESS;
        executing = true;

        new Thread(() -> {
            Utils.sendClickPacket(
                    screenHandler,
                    35,
                    0,
                    SlotActionType.PICKUP,
                    compass
            );

            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ScreenHandler currentScreenHandler = MinecraftClient.getInstance().player.currentScreenHandler;
                ItemStack sign = currentScreenHandler.slots.get(3).getStack();

                if (!sign.getName().getString().contains("Add Name Contains Filter")) continue;

                Utils.sendClickPacket(
                        currentScreenHandler,
                        3,
                        0,
                        SlotActionType.PICKUP,
                        sign
                );
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(name.replaceAll(" \\[.*]|[^A-Za-z0-9 \\-']", ""));

            for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ScreenHandler currentScreenHandler = MinecraftClient.getInstance().player.currentScreenHandler;
                ItemStack searchItem = currentScreenHandler.slots.get(53).getStack();
                if (!searchItem.getName().getString().contains("Search")) continue;

                Utils.sendClickPacket(
                        currentScreenHandler,
                        53,
                        0,
                        SlotActionType.PICKUP,
                        searchItem
                );
                break;
            }

            executing = false;
        }).start();

        // Cancels the regular middle click
        return ActionResult.FAIL;
    }
}
