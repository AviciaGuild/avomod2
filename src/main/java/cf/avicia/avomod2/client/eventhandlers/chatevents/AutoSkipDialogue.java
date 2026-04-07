package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.AvoMod2Client;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoSkipDialogue {
    private static long lastRun = 0;

    private static KeyBinding keyBinding;

    public static void initKeybind() {
        keyBinding = new KeyBinding("Toggle auto skip dialogue", InputUtil.GLFW_KEY_PERIOD, AvoMod2Client.avomodCategory);
        KeyBindingRegistryImpl.registerKeyBinding(keyBinding);
    }

    private record DialogueOption(String message, boolean isRequiredChoice, int index) {}

    public static Text onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("skipDialogue")) return message;
        KeyBinding sneakKeyBind = MinecraftClient.getInstance().options.sneakKey;
        if ((message.getString().contains("  Press SHIFT to continue") || message.getString().contains("  Press SNEAK to continue"))) {
            Thread thread = new Thread(() -> {
                try {
                    sneakKeyBind.setPressed(true);
                    Thread.sleep(100);
                    sneakKeyBind.setPressed(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        return message;
    }

    public static void onTick() {
        if (keyBinding.wasPressed()) {
            boolean autoSkipDialogues = ConfigsHandler.getConfigBoolean("skipDialogue");
            ConfigsHandler.updateConfigs("skipDialogue", (!autoSkipDialogues) ? "Enabled" : "Disabled");
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(autoSkipDialogues ? "§7Auto dialogue skip §cDisabled" : "§7Auto dialogue skip §aEnabled"));
        }
        if (!ConfigsHandler.getConfigBoolean("skipDialogue")) return;
        final long MIN_DIFF_MS = 200;
        if (System.currentTimeMillis() - lastRun < MIN_DIFF_MS) return;
        trySkipDialogue(MinecraftClient.getInstance().inGameHud.overlayMessage);
        lastRun = System.currentTimeMillis();
    }

    private static void trySneak() {
        KeyBinding sneakKeyBind = MinecraftClient.getInstance().options.sneakKey;
        Thread thread = new Thread(() -> {
            try {
                sneakKeyBind.setPressed(true);
                Thread.sleep(200);
                sneakKeyBind.setPressed(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private static void scroll(boolean up) {
        var client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() != null && client.player != null) {
            client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket((client.player.getInventory().getSelectedSlot() + (up ? -1 : 1)) % 8));
        }
    }

    private static void trySkipDialogue(Text message) {
        if (!ConfigsHandler.getConfigBoolean("skipDialogue")) return;
        if (!isSkippableDialogueOpen(message)) return;
        if (doesDialogueHaveOptions(message)) {
            int scrollNeeded = getScrollNeededToProgress(message);
            if (scrollNeeded == Integer.MAX_VALUE) return; // Wait until we get actual info
            if (scrollNeeded == 0) {
                // No scrolling needed, just sneak
                trySneak();
            } else {
                scroll(scrollNeeded > 0);
            }
        } else {
            trySneak();
        }
    }

    private static boolean isSkippableDialogueOpen(Text message) {
        if (message == null) {
            return false;
        }
        if (message.getString().equals(" to confirm") || message.getString().equals(" to continue")) {
            return true;
        }
        boolean isOpen = false;
        for (var sibling : message.getSiblings()) {
            isOpen = isOpen || isSkippableDialogueOpen(sibling);
        }
        return isOpen;
    }

    private static boolean doesDialogueHaveOptions(Text message) {
        if (message == null) {
            return false;
        }
        if (message.getString().equals(" to choose option")) {
            return true;
        }
        boolean hasOptions = false;
        for (var sibling : message.getSiblings()) {
            hasOptions = hasOptions || doesDialogueHaveOptions(sibling);
        }
        return hasOptions;
    }

    private static List<DialogueOption> getDialogueOptions(Text message) {
        if (message == null) return new ArrayList<>();
        if (!message.getSiblings().isEmpty() && message.getStyle() != null && message.getStyle().getFont() instanceof StyleSpriteSource.Font(
                net.minecraft.util.Identifier id
        ) && id.toString().contains("choice")) {
            String idRegex = ".*choice_(?<id>\\d+)";
            Pattern pattern = Pattern.compile(idRegex);
            Matcher matcher = pattern.matcher(id.toString());
            if (matcher.find()) {
                int index = Integer.parseInt(matcher.group("id"));
                boolean isRequiredChoice = hasSpecificOption(message);
                String content = message.getString();
                return List.of(new DialogueOption(content, isRequiredChoice, index));
            }
        }
        List<DialogueOption> options = new ArrayList<>();
        for (var sibling : message.getSiblings()) {
            options.addAll(getDialogueOptions(sibling));
        }
        return options;
    }

    // These have just been manually brute forced, but it seems like they are consistent.
    // The indexing works in such a way that the bottom option is always index 3 and if there are more options it can
    // go down to index 0 for the topmost option. At most 4 options at once can be shown.
    private static final String OPT0_A = "\uDB00\uDC23\uDAFF\uDFF5\uE090 \uDB00\uDC02\uE041\uDB00\uDC02\uDAFF\uDF48";
    private static final String OPT0_B = "\uDB00\uDC25\uDAFF\uDFF5\uE090 \uE041\uDAFF\uDF4A";

    private static final String OPT1_A = "\uDB00\uDC23\uDAFF\uDFF5\uE080 \uDB00\uDC02\uE031\uDB00\uDC02\uDAFF\uDF48";
    private static final String OPT1_B = "\uDB00\uDC25\uDAFF\uDFF5\uE080 \uE031\uDAFF\uDF4A";

    private static final String OPT2_A = "\uDB00\uDC25\uDAFF\uDFF5\uE070 \uE021\uDAFF\uDF4A";
//    private static final String OPT2_A = "\uDB00\uDC23\uDB00\uDC02\uE010\uDB00\uDC02\uDAFF\uDF48";
    private static final String OPT2_B = "\uDB00\uDC25\uDAFF\uDFF5\uE070 \uE021\uDAFF\uDF4A";

    private static final String OPT3_A = "\uDB00\uDC25\uDAFF\uDFF5\uE060 \uE011\uDAFF\uDF4A";
//    private static final String OPT3_A = "\uDB00\uDC23\uDB00\uDC02\uE020\uDB00\uDC02\uDAFF\uDF48";
    private static final String OPT3_B = "\uDB00\uDC25\uDAFF\uDFF5\uE060 \uE011\uDAFF\uDF4A";

    private static int getSelectedOptionIndex(Text message) {
        if (message == null) return -1;
        if (!message.getSiblings().isEmpty() && message.getStyle() != null && message.getStyle().getFont() instanceof StyleSpriteSource.Font(
                net.minecraft.util.Identifier id
        ) && id.toString().endsWith("choice")) {
            String msg = message.getString();
            if (msg.contains(OPT0_A) || msg.contains(OPT0_B)) return 0;
            if (msg.contains(OPT1_A) || msg.contains(OPT1_B)) return 1;
            if (msg.contains(OPT2_A) || msg.contains(OPT2_B)) return 2;
            if (msg.contains(OPT3_A) || msg.contains(OPT3_B)) return 3;

            return -1;
        }
        for (var sibling : message.getSiblings()) {
            int siblingIndex = getSelectedOptionIndex(sibling);
            if (siblingIndex != -1) {
                return siblingIndex;
            }
        }
        return -1;
    }

    private static boolean hasSpecificOption(Text message) {
        if (message == null) {
            return false;
        }
        if (message.getStyle().getColor() != null && message.getStyle().getColor().getName().equals("light_purple")) {
            return true;
        }
        boolean hasSpecific = false;
        for (var sibling : message.getSiblings()) {
            hasSpecific = hasSpecific || hasSpecificOption(sibling);
        }
        return hasSpecific;
    }

    private static int compareToRequiredOption(Text message) {
        var options = getDialogueOptions(message);
        var selectedIndex = getSelectedOptionIndex(message);

        if (selectedIndex == -1) return Integer.MAX_VALUE; // nothing selected

        // There can be multiple required options (for some reason) so if the currently selected one is required just go with ti
        for (var option : options) {
            if (option.isRequiredChoice && option.index == selectedIndex) {
                return 0;
            }
        }

        for (var option : options) {
            if (option.isRequiredChoice) {
                return selectedIndex - option.index;
            }
        }

        return Integer.MAX_VALUE; // no required option found
    }

    private static int getScrollNeededToProgress(Text message) {
        if (!hasSpecificOption(message)) return 0;
        return compareToRequiredOption(message);
    }
}
