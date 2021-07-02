package ru.hapyl.classesfight.experience.reward;

import org.bukkit.Material;

public class RarePackageReward extends RareReward {

    private final Material material;
    private final int amount;
    private long coins;
    private long exp;
    private long ruby;

    public RarePackageReward(String name, Material material, int amount) {
        super(name, "");
        this.material = material;
        this.amount = amount;
    }


    public RarePackageReward(String name, Material material) {
        this(name, material, 1);
    }

    public long getCoins() {
        return coins;
    }

    public int getAmount() {
        return amount;
    }

    public RarePackageReward addCoins(long coins) {
        this.coins += coins;
        return this;
    }

    public long getExp() {
        return exp;
    }

    public RarePackageReward addExp(long exp) {
        this.exp += exp;
        return this;
    }

    public long getRuby() {
        return ruby;
    }

    public Material getMaterial() {
        return material;
    }

    public RarePackageReward addRuby(long ruby) {
        this.ruby += ruby;
        return this;
    }
}
