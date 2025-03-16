package cf.avicia.avomod2.inventoryoverlay.item;

import java.util.List;

public class Requirements {
    public int level;
    public LevelRange levelRange;
    public int strength, dexterity, intelligence, defence, agility;
    public String quest;
    public String classRequirement;
    public List<String> skills; // Uses custom deserializer
}
