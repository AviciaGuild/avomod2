package cf.avicia.avomod2.client.eventhandlers.screenevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AttackedTerritoryDifficulty {

    private static long currentTime = System.currentTimeMillis();
    private static String currentTerritory = null;
    private static String currentDefense = null;

    public static void beforeRender(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight, Screen screen1, MatrixStack matrices, int mouseX, int mouseY) {
        if (ConfigsHandler.getConfigBoolean("disableAll") || client.player == null || screen == null) return;
        if (!(screen1 instanceof GenericContainerScreen) || !screen1.getTitle().getString().contains("Attacking: "))
            return;
        try {
            String territoryDefense = null;

            DefaultedList<Slot> containerSlots = client.player.currentScreenHandler.slots;
            ItemStack attackInfoItem = containerSlots.get(13).getStack();
            List<Text> territoryLore = attackInfoItem.getTooltip(client.player, TooltipContext.Default.ADVANCED);
            String territoryDefenseMessage = territoryLore.get(1).getString();

            Optional<Text> timerTextOptional = territoryLore.stream().filter(line ->
                    Objects.requireNonNull(Utils.getUnformattedString(line.getString())).startsWith("Time to Start")).findFirst();
            if (timerTextOptional.isEmpty()) return;

            String timerText = timerTextOptional.get().getString();
            String[] timerSplit = timerText.split(": ");

            if (timerSplit.length < 2) return;
            String timer = Utils.getUnformattedString(timerSplit[1].split("m")[0]);
            if (timer == null) return;

            if (territoryDefenseMessage.contains("Territory Defences")) {
                String unformattedTerritoryDefenseMessage = Utils.getUnformattedString(territoryDefenseMessage);

                territoryDefense = unformattedTerritoryDefenseMessage.split(": ")[1];
            }

            if (territoryDefense == null) return;

            currentDefense = territoryDefense;
            currentTerritory = screen1.getTitle().getString().split(": ")[1];
            currentTime = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ActionResult onMessage(Text message) {
        if (ConfigsHandler.getConfigBoolean("disableAll") ||
                !ConfigsHandler.getConfigBoolean("terrDefenseInChat")) return ActionResult.SUCCESS;
        String unformattedMessage = Utils.getUnformattedString(message.getString());
        if (unformattedMessage == null) return ActionResult.SUCCESS;

        if (unformattedMessage.contains("The war for") && unformattedMessage.endsWith("minutes.")) {
            String territory = unformattedMessage.split("for ")[1].split(" will")[0];
            if (System.currentTimeMillis() - currentTime > 5000 || !territory.equals(currentTerritory))
                return ActionResult.SUCCESS;

            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                MinecraftClient.getInstance().getNetworkHandler().sendCommand(String.format("g %s defense is %s", currentTerritory, currentDefense));
            }
        }
        return ActionResult.SUCCESS;
    }

}
