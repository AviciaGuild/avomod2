package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.configs.locations.LocationsHandler;
import cf.avicia.avomod2.client.locationselements.Element;
import cf.avicia.avomod2.client.locationselements.ElementGroup;
import cf.avicia.avomod2.client.locationselements.RectangleElement;
import cf.avicia.avomod2.client.locationselements.TextElement;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.awt.*;
import java.util.List;
import java.util.*;

public class WarDPS {
    public static long warStartTime = -1;
    public static long firstDamageTime = -1;
    public static String previousTerritoryName = "";
    public static long lastTimeInWar = 0;
    private static long previousTime = 0;
    private static double previousEhp = 0;
    private static double dps = 0;
    private static List<Double> previousFiveEhp = new ArrayList<>();
    private static double dpsFiveSec = 0;
    private static double maxEhp = 0;
    private static double dpsSinceStart = 0;
    private static double timeRemaining = 0;
    private static String initialStats = "";
    private static String latestStats = "";
    private static double ehpDisplay = 0;
    private static double lowerDpsDisplay = 0;
    private static double higherDpsDisplay = 0;
    private static long timeDisplay = 0;


    public static void execute(String[] bossBarWords) {
        try {
            if (System.currentTimeMillis() - lastTimeInWar > 119 * 1000) {
                // If the last war happened more than 2 minutes ago, reset the previous territory name,
                // in case you war the same territory twice in a row
                previousTerritoryName = "";
            }

            lastTimeInWar = System.currentTimeMillis();
            int startIndex1 = Arrays.asList(bossBarWords).indexOf("-");
            int startIndex2 = Arrays.asList(bossBarWords).lastIndexOf("-");
            StringBuilder territoryName = new StringBuilder();
            for (int i = 1; i < startIndex1 - 1; i++) {
                territoryName.append(Utils.getUnformattedString(bossBarWords[i])).append(" ");
            }

            if (!territoryName.toString().equals(previousTerritoryName)) {
                newWar();
                previousTerritoryName = territoryName.toString();
                warStartTime = System.currentTimeMillis();
                initialStats = String.join(" ", bossBarWords);
            }
            latestStats = String.join(" ", bossBarWords);

            String health = Objects.requireNonNull(Utils.getUnformattedString(bossBarWords[startIndex1 + 2]));
            String defense = Objects.requireNonNull(Utils.getUnformattedString(bossBarWords[startIndex1 + 3]))
                    .replace("(", "").split("\\)")[0].replace("%", "");
            String damage = Objects.requireNonNull(Utils.getUnformattedString(bossBarWords[startIndex2 + 2]));
            String attacks = Objects.requireNonNull(Utils.getUnformattedString(bossBarWords[startIndex2 + 3]))
                    .replace("(", "").split("\\)")[0].replace("x", "");

            ehpDisplay = Math.round(Double.parseDouble(health) / (1.0 - (Double.parseDouble(defense) / 100.0)));
            lowerDpsDisplay = Double.parseDouble(damage.split("-")[0]) * Double.parseDouble(attacks);
            higherDpsDisplay = Double.parseDouble(damage.split("-")[1]) * Double.parseDouble(attacks);

            if (maxEhp == 0) {
                maxEhp = ehpDisplay;
                previousEhp = ehpDisplay;
                previousFiveEhp.add(ehpDisplay);
            }

            timeDisplay = (System.currentTimeMillis() - warStartTime) / 1000;
            if (timeDisplay != previousTime) {
                dps = previousEhp - ehpDisplay;
                previousEhp = ehpDisplay;

                if (firstDamageTime == -1 && dps > 0) {
                    firstDamageTime = System.currentTimeMillis();
                }

                if (previousFiveEhp.size() == 5) {
                    previousFiveEhp.remove(0);
                }

                previousFiveEhp.add(ehpDisplay);
                dpsFiveSec = Math.floor((previousFiveEhp.get(0) - ehpDisplay) / 5);

                if (firstDamageTime != -1 && System.currentTimeMillis() - firstDamageTime > 0) {
                    dpsSinceStart = (maxEhp - previousEhp) / ((System.currentTimeMillis() - firstDamageTime) / 1000.0);
                    timeRemaining = Math.floor(previousEhp / dpsSinceStart);
                }
            }

            previousTime = timeDisplay;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void render(MatrixStack matrices) {
        if (System.currentTimeMillis() - lastTimeInWar < 2000) {
            getElementsToDraw(timeDisplay, ehpDisplay, lowerDpsDisplay, higherDpsDisplay).draw(matrices);
        }
    }

    public static void warEnded(boolean warWon) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§dTime in War: §b" +
                ((System.currentTimeMillis() - warStartTime) / 1000) + "s"));
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§dAverage DPS: §b" +
                String.format("%,.0f", (warWon ? maxEhp : maxEhp - previousEhp) / ((System.currentTimeMillis() - firstDamageTime) / 1000f)).replace(" ", ",")));
        String[] statsSplit = initialStats.split(" - ");
        if (statsSplit.length >= 2) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§dInitial Tower Stats: " +
                    String.join(" - ", Arrays.copyOfRange(statsSplit, 1, statsSplit.length))));
        }

        if (!warWon) {
            String[] latestStatsSplit = latestStats.split(" - ");
            if (latestStatsSplit.length >= 2) {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§dFinal Tower Stats: " +
                        String.join(" - ", Arrays.copyOfRange(latestStatsSplit, 1, latestStatsSplit.length))));
            }
        }

        newWar();
    }

    private static void newWar() {
        warStartTime = -1;
        firstDamageTime = -1;
        previousTime = 0;
        previousEhp = 0;
        previousFiveEhp = new ArrayList<>();
        dps = 0;
        dpsFiveSec = 0;
        dpsSinceStart = 0;
        maxEhp = 0;
        timeRemaining = 0;
        previousTerritoryName = "";
        initialStats = "";
        latestStats = "";

        AuraHandler.resetAura();
    }

    public static ElementGroup getElementsToDraw(long time, double towerEhp, double lowerTowerDps, double upperTowerDps) {
        String[] stats = new String[]{
                String.format("%s Seconds", time),
                String.format("Tower EHP: §b%s", Utils.getReadableNumber(towerEhp, 2)),
                String.format("Tower DPS: §b%s-%s", Utils.getReadableNumber(lowerTowerDps, 2), Utils.getReadableNumber(upperTowerDps, 2)),
                String.format("Team DPS/1s: §c%s", Utils.getReadableNumber(dps, 2)),
                String.format("Team DPS/5s: §c%s", Utils.getReadableNumber(dpsFiveSec, 2)),
                String.format("Team DPS (total): §e%s", Utils.getReadableNumber(dpsSinceStart, 2)),
                String.format("Estimated Time Remaining: §a%ss", (int) timeRemaining)
        };

        if (dpsSinceStart == 0) {
            stats[6] = "Estimated Time Remaining: Unknown";
        }

        int maxWidth = Collections.max(Arrays.stream(stats).map(stat -> MinecraftClient.getInstance().textRenderer.getWidth(stat)).toList());
        List<Element> elementList = new ArrayList<>();
        float y = LocationsHandler.getStartY("warDPS", 1F);
        float x = LocationsHandler.getStartX("warDPS", maxWidth + 10, 1F);

        elementList.add(new RectangleElement(x, y, maxWidth + 10, 18 + (10 * stats.length), new Color(0, 0, 0, 100)));
        elementList.add(new TextElement("§bWar Info", x + 4, y + 4, Color.CYAN));

        int additionalHeight = 15;
        for (String stat : stats) {
            x = LocationsHandler.getStartX("warDPS", MinecraftClient.getInstance().textRenderer.getWidth(stat), 1F);
            elementList.add(new TextElement(stat, x + 4, y + additionalHeight, Color.WHITE));
            additionalHeight += 10;
        }

        return new ElementGroup("warDPS", 1F, elementList);
    }

    public static ActionResult onMessage(Text message) {

        String unformattedMessage = Utils.getUnformattedString(Utils.textWithoutTimeStamp(message).getString());
        if (unformattedMessage == null) return ActionResult.SUCCESS;

        if (ConfigsHandler.getConfigBoolean("dpsInWars") && System.currentTimeMillis() - lastTimeInWar < 5000 && unformattedMessage.contains(previousTerritoryName.trim())) {
            // If you saw a tower health bar less than 5 seconds ago (if you're in a war)
            if (unformattedMessage.startsWith("[WAR] You have taken control of ")) {
                warEnded(true);
            }
            if (unformattedMessage.startsWith("[WAR] Your guild has lost the war for ") || unformattedMessage.startsWith("Your active attack was canceled and refunded to your headquarter")) {
                warEnded(false);
            }
        }
        return ActionResult.SUCCESS;
    }

    public static ActionResult onRenderBossBar(MatrixStack matrices, int x, int y, BossBar bossBar) {
        try {
            String bossBarText = bossBar.getName().getString();
            String[] bossBarWords = bossBarText.split(" ");


            if (ConfigsHandler.getConfigBoolean("dpsInWars") && bossBarText.contains("Tower") && bossBarWords.length >= 6) {
                try {
                    execute(bossBarWords);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ActionResult.SUCCESS;
    }
}
