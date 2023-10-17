package cf.avicia.avomod2.webrequests.wynnapi;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.webrequests.WebRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class PlayerList {

    private JsonObject playerListData = null;

    public PlayerList() {
        try {
            this.playerListData = new Gson().fromJson(WebRequest.getData("https://api.wynncraft.com/v3/player"), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayerOnline(String username) {
        for (Map.Entry<String, JsonElement> player : playerListData.getAsJsonObject("players").entrySet()) {
            if (player.getKey().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public JsonArray getWorldPlayers(String world) {
        try {
            JsonArray worldPlayers = new JsonArray();
            for (Map.Entry<String, JsonElement> player : playerListData.getAsJsonObject("players").entrySet()) {
                if (player.getValue().getAsString().equals(Utils.getFormattedWorld(world))) {
                    worldPlayers.add(player.getKey());
                }
            }
            return worldPlayers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getAmountOfWorldPlayers(String world) {
        try {
            return getWorldPlayers(world).size();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
