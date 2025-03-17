package cf.avicia.avomod2.client.eventhandlers.screenevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.eventhandlers.chatevents.ShowRealName;
import cf.avicia.avomod2.client.eventhandlers.hudevents.attacktimermenu.AttackTimerMenu;
import cf.avicia.avomod2.client.eventhandlers.hudevents.attacktimermenu.ChatDefenseInfo;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttackedTerritoryDifficulty {

    private static long currentTime = System.currentTimeMillis();
    private static String currentTerritory = null;
    private static String currentDefense = null;

    public static void beforeRender(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight, Screen screen1, DrawContext drawContext, int mouseX, int mouseY) {
        if (ConfigsHandler.getConfigBoolean("disableAll") || client.player == null || screen == null) return;
        if (!(screen1 instanceof GenericContainerScreen) || !screen1.getTitle().getString().contains("Attacking: "))
            return;
        try {
            String territoryDefense = null;

            DefaultedList<Slot> containerSlots = client.player.currentScreenHandler.slots;
            ItemStack attackInfoItem = containerSlots.get(13).getStack();
            List<Text> territoryLore = attackInfoItem.getTooltip(Item.TooltipContext.DEFAULT, client.player, TooltipType.ADVANCED);
            if (territoryLore.size() < 2) {
                return;
            }
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

    public static Text onMessage(Text message) {
        if (ConfigsHandler.getConfigBoolean("disableAll")) return message;
        String unformattedMessage = Utils.getUnformattedString(message.getString());
        if (unformattedMessage == null) return message;

        if (ConfigsHandler.getConfigBoolean("terrDefenseInChat") && unformattedMessage.contains("The war for") && unformattedMessage.endsWith("minutes.")) {
            String territory = unformattedMessage.split("for ")[1].split(" will")[0];
            if (System.currentTimeMillis() - currentTime > 5000 || !territory.equals(currentTerritory))
                return message;

            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                MinecraftClient.getInstance().getNetworkHandler().sendCommand(String.format("g %s defense is %s", currentTerritory, currentDefense));
            }
        }

        String regex = "(?:\uDAFF\uDFFC\uE006\uDAFF\uDFFF\uE002\uDAFF\uDFFE|\uDAFF\uDFFC\uE001\uDB00\uDC06) [^ ]* (?<username>[^:]+): (?<territory>.+) defense is (?<defense>Very Low|Low|Medium|High|Very High).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(unformattedMessage);

        if (matcher.find()) {
            String username = matcher.group("username");
            String territory = matcher.group("territory");
            String defense = matcher.group("defense");
            String realName = ShowRealName.getRealName(message);
            if (realName != null) {
                username = realName;
            }

            AttackTimerMenu.chatDefenses.put(territory, new ChatDefenseInfo(username, territory, defense, System.currentTimeMillis()));
        }

        return message;
    }

}
