package cf.avicia.avomod2.client.eventhandlers.hudevents.attacktimermenu;

public record ChatDefenseInfo(String username, String territory, String defense, long timestamp) {
    public boolean isRecent() {
        return System.currentTimeMillis() - timestamp < 40 * 60 * 1000;
    }
}
