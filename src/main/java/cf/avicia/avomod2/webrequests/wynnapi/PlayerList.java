package cf.avicia.avomod2.webrequests.wynnapi;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.webrequests.WebRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

public class PlayerList {

    private JsonObject playerListData = null;

    public PlayerList() {
        try {
            this.playerListData = new Gson().fromJson(WebRequest.getData("https://api.wynncraft.com/public_api.php?action=onlinePlayers"), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPlayerOnline(String username) {
        Set<Map.Entry<String, JsonElement>> worlds = playerListData.entrySet();
        for (Map.Entry<String, JsonElement> world : worlds) {
            if (world.getKey().equals("request")) continue;
            JsonArray players = world.getValue().getAsJsonArray();
            for (JsonElement player : players) {
                if (player.getAsString().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    public JsonArray getWorldPlayers(String world) {
        try {
            return this.playerListData.getAsJsonArray(Utils.getFormattedWorld(world));
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
