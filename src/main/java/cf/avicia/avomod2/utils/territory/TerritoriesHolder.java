package cf.avicia.avomod2.utils.territory;

import java.util.Map;

public class TerritoriesHolder {
    private Map<String, Territory> territories;

    public TerritoriesHolder(Map<String, Territory> territories) {
        this.territories = territories;
    }

    public Map<String, Territory> getTerritories() { return territories; }
    public void setTerritories(Map<String, Territory> territories) { this.territories = territories; }
}
