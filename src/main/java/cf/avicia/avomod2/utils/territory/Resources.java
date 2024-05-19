package cf.avicia.avomod2.utils.territory;

public class Resources {
    private int emeralds;
    private int ore;
    private int crops;
    private int wood;
    private int fish;

    public Resources(int emeralds, int ore, int crops, int wood, int fish) {
        this.emeralds = emeralds;
        this.ore = ore;
        this.crops = crops;
        this.wood = wood;
        this.fish = fish;
    }

    public int getEmeralds() { return emeralds; }
    public void setEmeralds(int emeralds) { this.emeralds = emeralds; }

    public int getOre() { return ore; }
    public void setOre(int ore) { this.ore = ore; }

    public int getCrops() { return crops; }
    public void setCrops(int crops) { this.crops = crops; }

    public int getWood() { return wood; }
    public void setWood(int wood) { this.wood = wood; }

    public int getFish() { return fish; }
    public void setFish(int fish) { this.fish = fish; }
}