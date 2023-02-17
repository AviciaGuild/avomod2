package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.utils.*;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.configs.locations.LocationsHandler;
import cf.avicia.avomod2.client.locationselements.Element;
import cf.avicia.avomod2.client.locationselements.ElementGroup;
import cf.avicia.avomod2.client.locationselements.RectangleElement;
import cf.avicia.avomod2.client.locationselements.TextElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import oshi.util.tuples.Pair;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AttackTimerMenu {

    private static final HashMap<String, ScreenCoordinates> attackCoordinates = new HashMap<>();
    public static HashMap<String, Pair<String, Long>> savedDefenses = new HashMap<>();

    public static void render(MatrixStack matrixStack) {
        if (!ConfigsHandler.getConfigBoolean("attacksMenu")) return;
        ElementGroup elementsToDraw = getElementsToDraw(getUpcomingAttacks(), false);
        if (elementsToDraw != null) {
            elementsToDraw.draw(matrixStack);
        }
    }

    public static ElementGroup getElementsToDraw(List<String> upcomingAttacks, boolean sample) {
        if (MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().world == null) return null;
        List<Element> elementsList = new ArrayList<>();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
//        upcomingAttacks.addAll(Arrays.asList("13:47 Otherwordly Monolith", "5:23 Detlas", "9:52 Guild Hall"));

        if (upcomingAttacks.size() == 0) {
            BeaconManager.soonestTerritory = null;
            BeaconManager.soonestTerritoryLocation = null;
            BeaconManager.compassTerritory = null;
            BeaconManager.compassLocation = null;

            return null;
        }

        List<Pair<String, String>> upcomingAttacksSplit = new ArrayList<>();
        List<String> upcomingAttackTerritories = new ArrayList<>();

        for (String upcomingAttack : upcomingAttacks) {
            String[] words = upcomingAttack.split(" ");
            if (words.length < 2) return null;

            String time = words[0];
            String territory = String.join(" ", Arrays.copyOfRange(words, 1, words.length));

            upcomingAttacksSplit.add(new Pair<>(time, territory));
            upcomingAttackTerritories.add(territory);
        }

        List<String> terrsToRemove = new ArrayList<>();
        for (Map.Entry<String, Pair<String, Long>> savedDefense : savedDefenses.entrySet()) {
            if (!upcomingAttackTerritories.contains(savedDefense.getKey())) {
                terrsToRemove.add(savedDefense.getKey());
            }
        }

        if (!sample) {
            for (String terrToRemove : terrsToRemove) {
                savedDefenses.remove(terrToRemove);
                attackCoordinates.remove(terrToRemove);

                if (terrToRemove.equals(BeaconManager.compassTerritory)) {
                    BeaconManager.compassTerritory = null;
                    BeaconManager.compassLocation = null;
                }
            }
        }

        upcomingAttacksSplit.sort((o1, o2) -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date d1 = sdf.parse(o1.getA());
                Date d2 = sdf.parse(o2.getA());
                return (int) (d1.getTime() - d2.getTime());
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        });

        if (!sample) {
            if (!upcomingAttacksSplit.get(0).getB().equals(BeaconManager.soonestTerritory) || BeaconManager.soonestTerritoryLocation == null) {
                BeaconManager.soonestTerritory = upcomingAttacksSplit.get(0).getB();
                BeaconManager.soonestTerritoryLocation = TerritoryData.getMiddleOfTerritory(upcomingAttacksSplit.get(0).getB());
            }
        }

        int xPos = MinecraftClient.getInstance().player.getBlockPos().getX();
        int zPos = MinecraftClient.getInstance().player.getBlockPos().getZ();
        String currentTerritory = TerritoryData.territoryAtCoordinates(new Pair<>(xPos, zPos));

        final int rectangleHeight = 12;
        final float scale = 1F;

        float startY = LocationsHandler.getStartY("attacksMenu", scale);

        for (Pair<String, String> attack : upcomingAttacksSplit) {
            try {
                Pair<String, Long> savedDefense = savedDefenses.get(attack.getB());
                int minutes = Integer.parseInt(attack.getA().split(":")[0]);
                int seconds = Integer.parseInt(attack.getA().split(":")[1]);
                Long warTimestamp = (minutes * 60000L + seconds * 1000L) + System.currentTimeMillis();

                if (sample) {
                    savedDefense = new Pair<>("Retrieving...", warTimestamp);
                } else if (savedDefense == null || savedDefense.getA().equals("Unknown") || Math.abs(savedDefense.getB() - warTimestamp) > 10000) {
                    savedDefense = TerritoryData.getTerritoryDefense(attack.getB(), warTimestamp);
                    savedDefenses.put(attack.getB(), savedDefense);
                }

                String terrDefense = savedDefense.getA();
                if (terrDefense.equals("Low") || terrDefense.equals("Very Low")) {
                    terrDefense = "§a" + terrDefense;
                } else if (terrDefense.equals("Medium")) {
                    terrDefense = "§e" + terrDefense;
                } else {
                    terrDefense = "§c" + terrDefense;
                }

                String message = "§6" + attack.getB() + " (" + terrDefense + "§6) §b" + attack.getA();
                if (attack.getB().equals(currentTerritory)) {
                    message = "§d§l" + attack.getB() + "§6 (" + terrDefense + "§6) §b" + attack.getA();
                }

                int rectangleWidth = textRenderer.getWidth(message) + 4;
                float startX = LocationsHandler.getStartX("attacksMenu", rectangleWidth, scale);

                elementsList.add(new RectangleElement(startX, startY, rectangleWidth, rectangleHeight, new Color(100, 100, 100, 100)));
                elementsList.add(new TextElement(message, startX + 2, startY + 2, new Color(255, 170, 0)));
                if (!sample) {
                    attackCoordinates.put(attack.getB(), new ScreenCoordinates(startX, startY, startX + rectangleWidth, startY + rectangleHeight));
                }
                startY += rectangleHeight;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new ElementGroup("attacksMenu", 1F, elementsList);

    }

    public static ActionResult mouseClicked(double mouseX, double mouseY) {
        if (ConfigsHandler.getConfigBoolean("disableAll") || !ConfigsHandler.getConfigBoolean("attacksMenu"))
            return null;
        for (Map.Entry<String, ScreenCoordinates> attackCoordinate : attackCoordinates.entrySet()) {
            if (attackCoordinate.getValue().mouseIn((int) mouseX, (int) mouseY)) {
                Coordinates territoryLocation = TerritoryData.getMiddleOfTerritory(attackCoordinate.getKey());
                BeaconManager.compassLocation = territoryLocation;

                if (BeaconManager.compassLocation != null) {
                    MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Utils.makeMessageThatRunsCommand("A blue beacon beam has been created in " + attackCoordinate.getKey() + " at (§3§n" + territoryLocation.x() + ", " + territoryLocation.z() + "§f)", String.format("/compass %s %s", territoryLocation.x(), territoryLocation.z())));
                    BeaconManager.compassTerritory = attackCoordinate.getKey();
                } else {
                    MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("Not a correct territory name (probably too long for the scoreboard)"));
                }
            }
        }

        return ActionResult.SUCCESS;
    }

    private static List<String> getUpcomingAttacks() {
        if (MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().world == null)
            return new ArrayList<>();

        Scoreboard scoreboard = MinecraftClient.getInstance().world.getScoreboard();
        Pattern attackTimerPattern = Pattern.compile("§b- \\d\\d:\\d\\d §3.*", Pattern.CASE_INSENSITIVE);

        List<String> upcomingAttacks = new ArrayList<>();
        for (ScoreboardObjective score : scoreboard.getObjectives()) {
            for (ScoreboardPlayerScore allPlayerScore : scoreboard.getAllPlayerScores(score)) {
                if (attackTimerPattern.matcher(allPlayerScore.getPlayerName()).find()) {
                    upcomingAttacks.add(Utils.getUnformattedString(allPlayerScore.getPlayerName()).substring(2));
                }
            }
            break;
        }
        List<String> duplicateTerritories = new ArrayList<>();

        return upcomingAttacks.stream().filter(e -> {
            if (!duplicateTerritories.contains(e)) {
                duplicateTerritories.add(e);
                return true;
            }
            return false;
        }).collect(Collectors.toList());
    }
}
