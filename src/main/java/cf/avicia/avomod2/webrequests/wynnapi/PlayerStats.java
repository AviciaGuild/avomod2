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
            this.playerData = new Gson().fromJson(WebRequest.getData("https://api.wynncraft.com/v3/player/" + username), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JsonObject getPlayerData() {
        try {
            return playerData;
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
                    .get("server")
                    .getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTimeSinceLastJoin() {
        try {
            Instant lastJoinedInstant = Utils.parseTimestamp(Objects.requireNonNull(getPlayerData())
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

    public String getGuildName() {
        try {
            return Objects.requireNonNull(getGuildData())
                    .get("name").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getGuildTag() {
        try {
            return Objects.requireNonNull(getGuildData())
                    .get("prefix").getAsString();
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
