package ru.hapyl.classesfight.classes.iclass.extra;

import org.bukkit.entity.Player;

public class Combo {

    private final Player player;

    private int comboHit;
    private long lastComboHit;

    public Combo(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public int getComboIfPresent() {
        this.validateCombo();
        return this.comboHit;
    }

    public void reset() {
        this.comboHit = 0;
        this.lastComboHit = 0;
    }

    private void validateCombo() {
        if ((System.currentTimeMillis() - this.lastComboHit) >= 2500) {
            this.comboHit = 0;
        }
    }

    public void incrementCombo(int comboHit) {
        this.comboHit += comboHit;
        this.lastComboHit = System.currentTimeMillis();
    }

}
