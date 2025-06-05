package cf.avicia.avomod2.webrequests.aviciaapi;

import cf.avicia.avomod2.utils.Utils;
import cf.avicia.avomod2.webrequests.WebRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class UpTimes {

    private final java.util.Comparator<Map.Entry<String, JsonElement>> mapComparator =
            Comparator.comparingInt(m -> m.getValue().getAsJsonObject().get("age").getAsInt());
    private JsonObject upTimeData = null;

    public UpTimes() {
        try {
            String apiUrl = Utils.getApiUrl();
            if (!apiUrl.isEmpty()) {
                this.upTimeData = new Gson().fromJson(WebRequest.getData(apiUrl + "/up"), JsonObject.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Map.Entry<String, JsonElement>> getWorldUpTimeData() {
        if (upTimeData == null) {
            return null;
        }
        ArrayList<Map.Entry<String, JsonElement>> worldUpTimes = new ArrayList<>(upTimeData.entrySet());
        worldUpTimes.sort(mapComparator);
        return worldUpTimes;
    }

    public String getNewestWorld() {
        ArrayList<Map.Entry<String, JsonElement>> worldUpTimeData = getWorldUpTimeData();
        if (worldUpTimeData == null) {
            return null;
        }
        if (!worldUpTimeData.isEmpty()) {
            return worldUpTimeData.getFirst().getKey();
        }
        return null;
    }

    public boolean isUp(String world) {
        String formattedWorld = Utils.getFormattedWorld(world);
        return upTimeData != null && upTimeData.has(formattedWorld);
    }

    /**
     * @param world which world to get the age of
     * @return age in minutes
     */
    public int getAge(String world) {
        String formattedWorld = Utils.getFormattedWorld(world);
        if (isUp(formattedWorld)) {
            JsonObject worldData = upTimeData.getAsJsonObject(formattedWorld);
            Instant worldStartInstant = Instant.parse(worldData.get("startTime").getAsString());
            return (int) (((System.currentTimeMillis() - worldStartInstant.toEpochMilli()) / 60000));
        }
        return 0;
    }
}
