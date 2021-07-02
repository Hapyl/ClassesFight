package ru.hapyl.classesfight.cosmetics.kill;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.utils.ParticlesBuilder;

public class Blood extends AbstractEffect {

    public Blood() {
        super("Blood", "Spawn blood around the player.", 200, Material.REDSTONE, ShopItemRarity.RARE);
    }

    @Override
    public void display(Player player) {
        final Location at = player.getLocation();
        new ParticlesBuilder.BlockBreak(at.clone().add(0, .5, 0), Material.REDSTONE_BLOCK, .4, .4, .4, 20, .5).show();
        PlayerLib.playSound(at, Sound.BLOCK_STONE_BREAK, 1.0f);
    }
}
