package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class ReadableMobHealth {
    public static ActionResult onRenderBossBar(MatrixStack matrices, int x, int y, BossBar bossBar) {
        if (ConfigsHandler.getConfigBoolean("readableHealth")) {
            String bossBarText = Utils.getUnformattedString(bossBar.getName().getString());
            if (bossBarText != null) {
                int heartIconIndex = bossBarText.chars().boxed().toList().indexOf(10084);
                if (heartIconIndex != -1) {
                    String[] bossBarSplit = bossBarText.substring(0, heartIconIndex).split(" - ");
                    if (bossBarSplit.length > 1) {
                        String health = bossBarSplit[1];
                        try {
                            String nicerHealth = Utils.getReadableNumber(Double.parseDouble(health), 1);
                            String newBossBarText = bossBar.getName().getString().replace(health, nicerHealth);
                            bossBar.setName(Text.of(newBossBarText));
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    public static Text onRenderEntityLabel(Text label) {
        String unformattedLabel = Utils.getUnformattedString(label.getString());
        if (unformattedLabel != null && unformattedLabel.startsWith("[|||||")) {
            try {
                String unformattedHealth = unformattedLabel.replaceAll("[|\\[\\]]|( .*)", ""); // Removed all |[] and trailing debuffs such as poison
                String formattedHealth = Utils.getReadableNumber(Double.parseDouble(unformattedHealth), 1);
                boolean hasGray = label.getString().contains("§8");
                String formattedLabel = hasGray ? label.getString().replaceAll("§8", "").replace(unformattedHealth, "§8" + formattedHealth) : label.getString().replace(unformattedHealth, formattedHealth);
                return Text.of(formattedLabel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return label;
    }
}
