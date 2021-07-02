package ru.hapyl.classesfight.feature.perk;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;

import java.awt.image.DataBuffer;
import java.util.List;

public enum Perk {

    ULTIMATE_MADNESS("Ultimate Madness", "Reduces points required for ultimate to charge by 1."),
    ULTIMATE_MADNESS_2("Ultimate Madness II", "Reduces points required for ultimate to charge by 1."),
    SLITHERING("Slithering", "Grants speed boost at the start of the game."),
    LUCKY_SEVEN("Lucky Seven", "Most abilities have 7% chance to refresh instantly.");

    private final String name;
    private final String info;

    Perk(String name, String info) {
        this.name = name;
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }

    public void grantPerk(Player player) {
        Database.getDatabase(player).getPerkEntry().grantPerk(this);
    }

    public void revokePerk(Player player) {
        Database.getDatabase(player).getPerkEntry().revokePerk(this);
    }

    public boolean hasPerk(Player player) {
        return Database.getDatabase(player).getPerkEntry().hasPerk(this);
    }

    public void setEnabled(Player player, boolean flag) {
        Database.getDatabase(player).getPerkEntry().setEnabled(this, flag);
    }

    public boolean hasPerkAndEnabled(Player player) {
        return hasPerk(player) && isEnabled(player);
    }

    public boolean isEnabled(Player player) {
        return Database.getDatabase(player).getPerkEntry().isEnabled(this);
    }

    public static List<Perk> getPerks(Player player) {
        return Database.getDatabase(player).getPerkEntry().getPerks();
    }

    public static boolean hasAnyPerks(Player player) {
        return !Database.getDatabase(player).getPerkEntry().getPerks().isEmpty();
    }

}
