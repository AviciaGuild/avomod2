package cf.avicia.avomod2.client.eventhandlers.hudevents;

import cf.avicia.avomod2.utils.Utils;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SeasonRatingLeaderboardHelper {
    private static final HashMap<Integer, Integer> leaderboard = new HashMap<>();

    public static Text onRenderEntityLabel(Text label) {
        String unformattedLabel = Utils.getUnformattedString(label.getString());
        Pattern leaderboardPattern = Pattern.compile("\\d+ - .+ \\(\\d+ SR\\)");
        if (leaderboardPattern.matcher(unformattedLabel).find()) {
            try {
                int leaderboardPosition = Integer.parseInt(unformattedLabel.substring(0, unformattedLabel.indexOf(' ')));
                String guildName = unformattedLabel.substring(unformattedLabel.indexOf(' ') + 2, unformattedLabel.indexOf('(') - 1);
                int seasonRating = Integer.parseInt(unformattedLabel.substring(unformattedLabel.indexOf('(') + 1, unformattedLabel.indexOf(" SR)")));
                leaderboard.put(leaderboardPosition, seasonRating);
                String positionColorCodes = label.getString().substring(0, label.getString().indexOf(String.valueOf(leaderboardPosition)));
                Text res = Text.of("%s%s §7- §b%s §d(%,.0f SR)".formatted(positionColorCodes, leaderboardPosition, guildName, (double) seasonRating).replace(" ", ","));
                if (leaderboard.containsKey(leaderboardPosition + 1)) {
                    int seasonRatingDifference = seasonRating - leaderboard.get(leaderboardPosition + 1);
                    if (seasonRatingDifference >= 0) {
                        res.getSiblings().add(Text.of(" §a(+%,.0f)".formatted((double) seasonRatingDifference).replace(" ", ",")));
                    }
                }
                return res;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return label;
    }
}
