package cf.avicia.avomod2;

import java.util.Locale;

public class Utils {
    public static String firstLetterCapital(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public static String getReadableTime(int minutes) {
        return (minutes >= 1440.0 ? (int) Math.floor((minutes / 1440.0)) + "d " : "") + (int) (Math.floor((minutes % 1440) / 60.0)) + "h " + minutes % 60 + "m";
    }

    public static String getFormattedWorld(String world) {
        String formattedWorld = world;
        if (world.matches("^\\d+")) {
            formattedWorld = "WC" + world;
        }
        return formattedWorld.toUpperCase(Locale.ROOT);
    }

    public static String getUnformattedString(String string) {
        return string.replaceAll("§.", "");
    }
}
