package cf.avicia.avomod2.utils;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String firstLetterCapital(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static String getReadableTime(int minutes) {
        return (minutes >= 1440.0 ? (int) Math.floor((minutes / 1440.0)) + "d " : "") + (int) (Math.floor((minutes % 1440) / 60.0)) + "h " + minutes % 60 + "m";
    }
    public static String getReadableTimeFromMillis(long millis) {
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return (minutes >= 1 ? minutes + "m " : "") + seconds + "s";
    }

    public static boolean isKeyDown(int keyCode) {
        return GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), keyCode) == 1;
    }

    public static boolean isShiftDown() {
        return isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    public static boolean isCtrlDown() {
        return isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL) || isKeyDown(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }

    public static String getFormattedWorld(String world) {
        String formattedWorld = world;
        if (world.matches("^\\d+")) {
            formattedWorld = "WC" + world;
        }
        return formattedWorld.toUpperCase(Locale.ROOT);
    }

    public static String getUnformattedString(String string) {
        if (string == null) {
            return null;
        }
        return string.replaceAll("§.", "");
    }

    public static String getCurrentWorld() {
        try {
            String name = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler()).getPlayerListEntry(UUID.fromString("16ff7452-714f-3752-b3cd-c3cb2068f6af"))).getDisplayName()).getString();
            return name.substring(name.indexOf("[") + 1, name.indexOf("]"));
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    public static MutableText makeMessageThatRunsCommand(String message, String command) {
        MutableText messageRes = Text.literal(message);
        messageRes.fillStyle(messageRes.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command)));
        messageRes.fillStyle(messageRes.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("§7Click to run §f" + command))));
        return messageRes;
    }

    public static boolean inHuntedMode() {
        if (MinecraftClient.getInstance().player != null) {
            String itemName = MinecraftClient.getInstance().player.getInventory().getStack(8).toString();
            return itemName.contains("snow");
        }
        return false;
    }

    public static String getReadableNumber(double number, int decimals) {
        if (number >= 1000000000) {
            return String.format("%sB", Math.round(number / (1000000000 / Math.pow(10, decimals))) / Math.pow(10, decimals));
        } else if (number >= 1000000) {
            return String.format("%sM", Math.round(number / (1000000 / Math.pow(10, decimals))) / Math.pow(10, decimals));
        } else if (number >= 1000) {
            return String.format("%sK", Math.round(number / (1000 / Math.pow(10, decimals))) / Math.pow(10, decimals));
        }

        return String.valueOf((int) number);
    }

    public static ArrayList<String> getMatches(String text, String regex) {
        ArrayList<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    public static Text textWithoutTimeStamp(Text text) {
        // Removes the wynntils timestamp from the message if there is one
        return Text.literal(text.getString().replaceAll("§8\\[§7.+§8\\]§[rf] *", "")).setStyle(text.getStyle());
    }

    public static Text textWithoutDuplicate(Text text) {
        // Removes the message stack duplicate from the message if there is one
        return Text.literal(text.getString().replaceAll(" §7\\(\\d+\\)", "")).setStyle(text.getStyle());
    }

    public static String removePrivateUseChars(String inputStr) {
        // Regular expression pattern to match characters in the private use areas (PUA) (the special characters used by Wynncraft)
        return inputStr.replaceAll("[\uE000-\uF8FF\uD800-\uDBFF\uDC00-\uDFFF\u200B\u2064]","").trim();
    }

    public static String getChatMessageWithOnlyMessage(Text message) {
        return Utils.removePrivateUseChars(Utils.getUnformattedString(Utils.textWithoutDuplicate(Utils.textWithoutTimeStamp(message)).getString()));
    }

    public static List<Integer> getVisibleMessagesByMessageIndex(int index) {
        ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
        List<Integer> result = new ArrayList<>();
        int messageIndex = -1;
        for (int i = 0; i < chatHud.visibleMessages.size(); i++) {
            if (chatHud.visibleMessages.get(i).endOfEntry()) {
                ++messageIndex;
            }
            if (messageIndex == index) {
                // Add the indexes in reverse order so if they need to be removed it will not break
                result.addFirst(i);
            }
        }
        return result;
    }

    public static Text getChatMessageAt(double mouseX, double mouseY) {
        ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
        int index = chatHud.getMessageLineIndex(chatHud.toChatLineX(mouseX), chatHud.toChatLineY(mouseY));
        if (index != -1) {
            Map<Integer, Integer> visibleToMessageIndex = new HashMap<>();
            int messageIndex = -1;
            for (int i = 0; i < chatHud.visibleMessages.size(); i++) {
                if (chatHud.visibleMessages.get(i).endOfEntry()) {
                    ++messageIndex;
                }
                visibleToMessageIndex.put(i, messageIndex);
            }
            int clickedMessageIndex = visibleToMessageIndex.getOrDefault(index, -1);
            if (clickedMessageIndex != -1 && clickedMessageIndex < chatHud.messages.size()) {
                return chatHud.messages.get(clickedMessageIndex).content();
            }
        }
        return null;
    }

    public static void sendClickPacket(ScreenHandler screenHandler, int slot, int button, SlotActionType slotActionType, ItemStack itemStack) {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(
                    new ClickSlotC2SPacket(
                            screenHandler.syncId,
                            screenHandler.getRevision(),
                            slot,
                            button,
                            slotActionType,
                            itemStack,
                            Int2ObjectMaps.emptyMap()
                    )
            );
        }
    }

    public static Instant parseTimestamp(String timestamp) {
        if (timestamp.endsWith("000")) {
            return Instant.parse(timestamp.replace("000", "Z"));
        }
        return Instant.parse(timestamp);
    }

    public static String getApiUrl() {
        return String.format("https://%s/api", ConfigsHandler.getConfig("aviciaApiDomain").replaceAll("https?://", "").replaceAll("/$", ""));
    }
}
