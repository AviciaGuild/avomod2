package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.client.configs.ConfigsHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadableMobHealth {
    public static ActionResult onRenderBossBar(DrawContext drawContext, int x, int y, BossBar bossBar) {
        if (ConfigsHandler.getConfigBoolean("readableHealth")) {
            String bossBarText = Utils.getUnformattedString(bossBar.getName().getString());
            // Avoid formatting bossbar healths in wars, since it breaks stuff
            if (bossBarText != null && !(bossBarText.contains("Tower") && bossBarText.split(" ").length >= 6)) {
                Matcher unformattedHealthMatcher = Pattern.compile("(\\d{4,})").matcher(bossBarText);
                if (unformattedHealthMatcher.find()) {
                    String unformattedHealth = unformattedHealthMatcher.group(1);
                    String formattedHealth = Utils.getReadableNumber(Double.parseDouble(unformattedHealth), 1);
                    String formattedLabel = bossBar.getName().getString().replace(unformattedHealth, formattedHealth);
                    bossBar.setName(Text.of(formattedLabel));
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    public static Text onRenderEntityLabel(Text label) {
        String unformattedLabel = Utils.getUnformattedString(label.getString());
        if (unformattedLabel != null && unformattedLabel.contains("[|||||")) {
            Matcher unformattedHealthMatcher = Pattern.compile("(\\d{4,})").matcher(unformattedLabel);
            if (unformattedHealthMatcher.find()) {
                String unformattedHealth = unformattedHealthMatcher.group(1);
                String formattedHealth = Utils.getReadableNumber(Double.parseDouble(unformattedHealth), 1);
                boolean hasGray = label.getString().contains("§8");
                String formattedLabel = hasGray ? label.getString().replaceAll("§8", "").replace(unformattedHealth, "§8" + formattedHealth) : label.getString().replace(unformattedHealth, formattedHealth);
                return Text.of(formattedLabel);
            }
        }
        return label;
    }
}
