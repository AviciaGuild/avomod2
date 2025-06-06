package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.client.configs.locations.LocationsHandler;
import cf.avicia.avomod2.client.eventhandlers.chatevents.ShowRealName;
import cf.avicia.avomod2.client.locationselements.Element;
import cf.avicia.avomod2.client.locationselements.ElementGroup;
import cf.avicia.avomod2.client.locationselements.RectangleElement;
import cf.avicia.avomod2.client.locationselements.TextElement;
import cf.avicia.avomod2.utils.ScreenCoordinates;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BombBellTracker {
    private static final HashMap<String, ScreenCoordinates> bombBellCoordinates = new HashMap<>();
    private static final List<BombData> storedBombs = new ArrayList<>();

    public static ElementGroup getElementsToDraw(List<BombData> storedBombs) {
        List<BombData> bombsToRemove = storedBombs.stream().filter(storedBomb -> storedBomb.getTimeLeft() <= 0).toList();
        storedBombs.removeAll(bombsToRemove);
        storedBombs.sort(Comparator.comparing(BombData::getTimeLeft));

        final int rectangleHeight = 12;
        final float scale = 1F;
        ArrayList<Element> elementsList = new ArrayList<>();
        AtomicReference<Float> y = new AtomicReference<>(LocationsHandler.getStartY("bombBellTracker", scale));

        storedBombs.forEach(storedBomb -> {
            double timeLeft = storedBomb.getTimeLeft();
            int minutesLeft = (int) timeLeft / 60;
            int secondsLeft = (int) timeLeft % 60;
            String message = String.format("%s Bomb on %s - %02dm %02ds", storedBomb.getBombType().getBombName(), storedBomb.getWorld(), minutesLeft, secondsLeft);

            int rectangleWidth = MinecraftClient.getInstance().textRenderer.getWidth(message) + 4;
            float x = LocationsHandler.getStartX("bombBellTracker", rectangleWidth, scale);

            elementsList.add(new RectangleElement(x, y.get(), rectangleWidth, rectangleHeight, new Color(100, 100, 100, 100)));
            elementsList.add(new TextElement(message, x + 2, y.get() + 2, new Color(255, 251, 0)));
            bombBellCoordinates.put(storedBomb.getWorld(), new ScreenCoordinates(x, y.get(), x + rectangleWidth, y.get() + rectangleHeight));
            y.updateAndGet(v -> v + rectangleHeight);
        });

        return new ElementGroup("bombBellTracker", scale, elementsList);
    }

    public static List<BombData> getSampleData() {
        return Arrays.asList(
                new BombData("12", BombType.COMBAT_XP),
                new BombData("38", BombType.LOOT)
        );
    }

    private static boolean isDuplicateBomb(BombData bomb) {
        return storedBombs.stream().anyMatch(bombData -> bombData.getWorld().equals(bomb.getWorld()) &&
                bombData.getBombType().equals(bomb.getBombType()));
    }

    public static void render(DrawContext drawContext) {
        if (!ConfigsHandler.getConfigBoolean("bombBellTracker")) return;
        getElementsToDraw(storedBombs).draw(drawContext);
    }

    public static Text onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("bombBellTracker")) return message;

        String unformattedMessage = Utils.getUnformattedString(Utils.textWithoutTimeStamp(message).getString());
        if (unformattedMessage == null || !unformattedMessage.startsWith("[Bomb Bell]")) return message;

        ArrayList<String> matches = Utils.getMatches(unformattedMessage, "(?<= thrown a )[a-zA-Z ]+(?= Bomb on)|(?<= on )[A-Z]{2}\\d{1,4}");
        if (matches.size() != 2) return message;

        String bombName = matches.get(0);
        String world = matches.get(1);

        BombType bombType = BombType.getBombType(bombName);
        if(bombType == null) return message;

        BombData bombData = new BombData(world, bombType);

        if (!isDuplicateBomb(bombData)) {
            storedBombs.add(bombData);
        }

        return message;
    }

    public static ActionResult mouseClicked(double mouseX, double mouseY) {
        if (ConfigsHandler.getConfigBoolean("disableAll") || !ConfigsHandler.getConfigBoolean("bombBellTracker")
                || !ConfigsHandler.getConfigBoolean("bombBellSwitchWorld")) return ActionResult.SUCCESS;
        for (Map.Entry<String, ScreenCoordinates> bombBellCoordinate : bombBellCoordinates.entrySet()) {
            if (bombBellCoordinate.getValue().mouseIn((int) mouseX, (int) mouseY)) {
                if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                    MinecraftClient.getInstance().getNetworkHandler().sendCommand("switch " + bombBellCoordinate.getKey());
                }
            }
        }
        return ActionResult.SUCCESS;
    }

        private enum BombType {
        COMBAT_XP("Combat XP"),
        PROFESSION_XP("Profession XP"),
        PROFESSION_SPEED("Profession Speed"),
        DUNGEON("Dungeon"),
        LOOT("Loot");

        private final String bombName;

        BombType(String bombName) {
            this.bombName = bombName;
        }

        public static BombType getBombType(String bombName) {
            Optional<BombType> optional = Arrays.stream(values()).filter(e -> e.bombName.equals(bombName)).findFirst();

            return optional.orElse(null);
        }

        public static int getTimeRemaining(BombType bombType) {
            return switch (bombType) {
                case PROFESSION_SPEED, DUNGEON -> 600;
                default -> 1200;
            };
        }

        public String getBombName() {
            return bombName;
        }
    }

    private static class BombData {
        private final String world;
        private final long startTime;
        private final BombType bombType;

        public BombData(String world, BombType bombType) {
            this.world = world;
            this.bombType = bombType;

            startTime = System.currentTimeMillis();
        }

        public String getWorld() {
            return world;
        }

        public double getTimeLeft() {
            return BombType.getTimeRemaining(this.bombType) - ((System.currentTimeMillis() - startTime) / 1000.0);
        }

        public BombType getBombType() {
            return bombType;
        }
    }

}
