package cf.avicia.avomod2.utils.territory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Territory {
    private String defense;
    private Resources productions;
    private Resources storages;
    private Resources maxStorages;
    private boolean headquarters;

    public Territory(String defense, Resources productions, Resources storages, Resources maxStorages, boolean headquarters) {
        this.defense = defense;
        this.productions = productions;
        this.storages = storages;
        this.maxStorages = maxStorages;
        this.headquarters = headquarters;
    }

    public String getDefense() { return defense; }
    public void setDefense(String defense) { this.defense = defense; }

    public Resources getProductions() { return productions; }
    public void setProductions(Resources productions) { this.productions = productions; }

    public Resources getStorages() { return storages; }
    public void setStorages(Resources storages) { this.storages = storages; }
    public Resources getMaxStorages() { return maxStorages; }
    public void setMaxStorages(Resources maxStorages) { this.maxStorages = maxStorages; }

    public boolean isHeadquarters() { return headquarters; }
    public void setHeadquarters(boolean headquarters) { this.headquarters = headquarters; }

    public static Territory parseTerritory(String data, boolean headquarters) {
        String productionPattern = "\\+(\\d+)\\s(Emeralds|Ore|Crops|Wood|Fish)\\sper\\sHour";
        String storagePattern = "(\\d+)/(\\d+)\\sstored\\s([ⒷⒸⓀⒿ✦])";
        String defensePattern = "Territory\\sDefences:\\s(.+)\\sTrading";

        Resources productions = new Resources(0, 0, 0, 0, 0);
        Resources storages = new Resources(0, 0, 0, 0, 0);
        Resources maxStorages = new Resources(0, 0, 0, 0, 0);

        Pattern productionRegex = Pattern.compile(productionPattern);
        Matcher productionMatcher = productionRegex.matcher(data);
        while (productionMatcher.find()) {
            int value = Integer.parseInt(productionMatcher.group(1));
            String type = productionMatcher.group(2).toLowerCase();
            switch (type) {
                case "emeralds":
                    productions.setEmeralds(value);
                    break;
                case "ore":
                    productions.setOre(value);
                    break;
                case "crops":
                    productions.setCrops(value);
                    break;
                case "wood":
                    productions.setWood(value);
                    break;
                case "fish":
                    productions.setFish(value);
                    break;
            }
        }

        Pattern storageRegex = Pattern.compile(storagePattern);
        Matcher storageMatcher = storageRegex.matcher(data);
        while (storageMatcher.find()) {
            int value = Integer.parseInt(storageMatcher.group(1));
            int maxValue = Integer.parseInt(storageMatcher.group(2));
            String type = storageMatcher.group(3);
            switch (type) {
                case "Ⓑ":
                    storages.setEmeralds(value);
                    maxStorages.setEmeralds(maxValue);
                    break;
                case "Ⓒ":
                    storages.setOre(value);
                    maxStorages.setOre(maxValue);
                    break;
                case "Ⓚ":
                    storages.setWood(value);
                    maxStorages.setWood(maxValue);
                    break;
                case "Ⓙ":
                    storages.setFish(value);
                    maxStorages.setFish(maxValue);
                    break;
                case "✦":
                    storages.setCrops(value);
                    maxStorages.setCrops(maxValue);
                    break;
            }
        }

        Pattern defenseRegex = Pattern.compile(defensePattern);
        Matcher defenseMatcher = defenseRegex.matcher(data);
        String defense = defenseMatcher.find() ? defenseMatcher.group(1) : "Unknown Defense";

        return new Territory(defense, productions, storages, maxStorages, headquarters);
    }

}