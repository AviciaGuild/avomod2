package cf.avicia.avomod2.client.eventhandlers.chatevents;

import cf.avicia.avomod2.client.configs.ConfigsHandler;
import cf.avicia.avomod2.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

public class TerritoryTakenWarning {
    private static final String newWarMessagePrefix = "󏿼󏿿󏿾";
    private static final String repeatedWarMessagePrefix = "󏿼󐀆";
    private static final Pattern territoryTakenPattern = Pattern.compile("^\\[(.+)] has taken control of (.+)!");

    public static Text onMessage(Text message) {
        if (!ConfigsHandler.getConfigBoolean("territoryTakenSound")) {
            return message;
        }
        String unformattedMessage = Utils.getUnformattedString(Utils.textWithoutTimeStamp(message).getString()).replaceAll("&.", "");
        unformattedMessage = unformattedMessage.replaceAll(newWarMessagePrefix, "").replaceAll(repeatedWarMessagePrefix, "").replaceAll("\\s+", " ").trim();
        if (territoryTakenPattern.matcher(unformattedMessage).find()) {
            if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().world.playSound(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getBlockPos(), SoundEvents.GOAT_HORN_SOUNDS.get(1).value(), SoundCategory.MASTER, 1f, 10f);
                MinecraftClient.getInstance().world.playSound(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player.getBlockPos(), SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.MASTER, 1f, 10f);
            }
        }
        return message;
    }
}
