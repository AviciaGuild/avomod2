package cf.avicia.avomod2.webrequests.aviciaapi;

import cf.avicia.avomod2.Utils;
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
            this.upTimeData = new Gson().fromJson(WebRequest.getData("https://www.avicia.cf/api/up"), JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Map.Entry<String, JsonElement>> getWorldUpTimeData() {
        ArrayList<Map.Entry<String, JsonElement>> worldUpTimes = new ArrayList<>(upTimeData.entrySet());
        worldUpTimes.sort(mapComparator);
        return worldUpTimes;
    }

    public String getNewestWorld() {
        if (getWorldUpTimeData().size() > 0) {
            return getWorldUpTimeData().get(0).getKey();
        }
        return null;
    }

    public boolean isUp(String world) {
        String formattedWorld = Utils.getFormattedWorld(world);
        return upTimeData.has(formattedWorld);
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
