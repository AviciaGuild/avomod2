package cf.avicia.avomod2.webrequests.aviciaapi;

import cf.avicia.avomod2.webrequests.WebRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

public class GuildNameFromTag {
    JsonObject guildNameData = null;
    public GuildNameFromTag(String tag) {
        try {
            String tagApiResponse = WebRequest.getData("https://www.avicia.cf/api/tag/" + tag);
            if (!tagApiResponse.equals("null")) {
                this.guildNameData = new Gson().fromJson(tagApiResponse, JsonObject.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasMatch() {
        return guildNameData != null;
    }

    public String getName() {
        // Returns the name of the guild. Returns the oldest guild if multiple matches
        for (Map.Entry<String, JsonElement> match : getMatches()) {
            return match.getValue().getAsString();
        }
        return null;
    }

    public String getFormattedListOfMatches() {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<String, JsonElement> match : getMatches()) {
            res.append("[");
            res.append(match.getKey());
            res.append("]");
            res.append(": ");
            res.append(match.getValue().getAsString());
            res.append(", ");
        }
        res.deleteCharAt(res.lastIndexOf(","));
        return res.toString();
    }

    public Set<Map.Entry<String, JsonElement>> getMatches() {
        return guildNameData.entrySet();
    }

    public boolean hasMultipleMatches() {
        return guildNameData.size() > 1;
    }
}
