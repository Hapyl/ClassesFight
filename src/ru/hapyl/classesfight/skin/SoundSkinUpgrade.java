package ru.hapyl.classesfight.skin;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundSkinUpgrade extends SkinUpgrade {

    private final Sound killSound;
    private final Sound deathSound;

    public SoundSkinUpgrade(String name, Sound killSound, Sound deathSound) {
        super(name, Type.SOUND);
        this.killSound = killSound;
        this.deathSound = deathSound;
    }

    public void affectKill(Location location) {
        location.getWorld().playSound(location, this.killSound, SoundCategory.RECORDS, 10, 1.0f);
    }

    public void affectDeath(Location location) {
        location.getWorld().playSound(location, this.deathSound, SoundCategory.RECORDS, 10, 1.0f);
    }

    @Override
    public void affect(Player player) {

    }
}
