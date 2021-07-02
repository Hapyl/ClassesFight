package ru.hapyl.classesfight.cosmetics.contrail;

import org.bukkit.Material;
import org.bukkit.Particle;
import ru.hapyl.classesfight.cosmetics.ParticleContrail;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class MusicContrail extends ParticleContrail {

    public MusicContrail() {
        super("Music", "Leave a note behind you!", 300, Material.MUSIC_DISC_CAT, ShopItemRarity.COMMON, Particle.NOTE, 2);
    }

}
