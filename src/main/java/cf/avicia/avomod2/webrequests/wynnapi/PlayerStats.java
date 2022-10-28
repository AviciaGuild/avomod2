package cf.avicia.avomod2.webrequests.wynnapi;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.webrequests.WebRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.Objects;


public class PlayerStats {

    private JsonObject playerData = null;

    public PlayerStats(String username) {
        try {
            this.playerData = new Gson().fromJson(WebRequest.getData("https://api.wynncraft.com/v2/player/" + username + "/stats"), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonObject getPlayerData() {
        try {
            return playerData.getAsJsonArray("data")
                    .get(0).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUsername() {
        try {
            return Objects.requireNonNull(getPlayerData())
                    .get("username").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getServer() {
        try {
            return Objects.requireNonNull(getPlayerData())
                    .getAsJsonObject("meta")
                    .getAsJsonObject("location")
                    .get("server").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTimeSinceLastJoin() {
        try {
            Instant lastJoinedInstant = Instant.parse(Objects.requireNonNull(getPlayerData())
                    .getAsJsonObject("meta")
                    .get("lastJoin").getAsString());
            return Utils.getReadableTime((int) ((System.currentTimeMillis() - lastJoinedInstant.toEpochMilli()) / 60000));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JsonObject getGuildData() {
        try {
            return Objects.requireNonNull(getPlayerData())
                    .getAsJsonObject("guild");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getGuild() {
        try {
            return Objects.requireNonNull(getGuildData())
                    .get("name").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getGuildRank() {
        try {
            return Objects.requireNonNull(getGuildData())
                    .get("rank").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
