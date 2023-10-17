package cf.avicia.avomod2.webrequests.wynnapi;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.webrequests.WebRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class GuildStats {

    private JsonObject guildData = null;

    public GuildStats(String guildName) {
        try {
            this.guildData = new Gson().fromJson(WebRequest.getData("https://api.wynncraft.com/v3/guild/" + guildName.replaceAll(" ", "%20")), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        try {
            return guildData.get("name").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPrefix() {
        try {
            return guildData.get("prefix").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getWithRankFormatting(String member) {
        JsonArray members = getMembers();
        Map<String, String> rankStars = new HashMap<>();
//        rankStars.put("OWNER", "\u2605\u2605\u2605\u2605\u2605");
//        rankStars.put("CHIEF", "\u2605\u2605\u2605\u2605");
//        rankStars.put("STRATEGIST", "\u2605\u2605\u2605");
//        rankStars.put("CAPTAIN", "\u2605\u2605");
//        rankStars.put("RECRUITER", "\u2605");
//        rankStars.put("RECRUIT", "");
        rankStars.put("OWNER", "*****");
        rankStars.put("CHIEF", "****");
        rankStars.put("STRATEGIST", "***");
        rankStars.put("CAPTAIN", "**");
        rankStars.put("RECRUITER", "*");
        rankStars.put("RECRUIT", "");
        for (JsonElement jsonElement : members) {
            JsonObject memberData = jsonElement.getAsJsonObject();
            if (memberData.get("name").getAsString().equals(member)) {
                return rankStars.get(memberData.get("rank").getAsString().toUpperCase()) + memberData.get("name").getAsString();
            }
        }
        return member;
    }

    public JsonArray getMembers() {
        try {
            JsonArray guildMembers = new JsonArray();
            for (Map.Entry<String, JsonElement> rankMap : guildData.getAsJsonObject("members").entrySet()) {
                if (!rankMap.getValue().isJsonObject()) {
                    continue;
                }
                for (Map.Entry<String, JsonElement> member : rankMap.getValue().getAsJsonObject().entrySet()) {
                    JsonObject playerData = new JsonObject();
                    playerData.addProperty("name", member.getKey());
                    playerData.addProperty("rank", rankMap.getKey());
                    for (Map.Entry<String, JsonElement> memberStat : member.getValue().getAsJsonObject().entrySet()) {
                        playerData.add(memberStat.getKey(), memberStat.getValue());
                    }
                    guildMembers.add(playerData);
                }
            }
            return guildMembers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTimeInGuild(String memberUsername) {
        JsonArray members = getMembers();
        for (JsonElement jsonElement : members) {
            JsonObject memberData = jsonElement.getAsJsonObject();
            if (memberData.get("name").getAsString().equals(memberUsername)) {
                Instant instant = Utils.parseTimestamp(memberData.get("joined").getAsString());
                return Utils.getReadableTime((int) (((System.currentTimeMillis() - instant.toEpochMilli()) / 60000)));
            }
        }
        return "0 minutes";
    }

}
