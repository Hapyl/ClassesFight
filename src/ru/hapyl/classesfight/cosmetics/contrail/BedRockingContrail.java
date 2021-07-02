package ru.hapyl.classesfight.cosmetics.contrail;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.cosmetics.BlockContrail;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

public class BedRockingContrail extends BlockContrail {

    public BedRockingContrail() {
        super("Bed Rockin'", "The strongest of it's kind.", NOT_AVAILABLE_IN_SHOP, Material.BEDROCK, ShopItemRarity.LEGENDARY, Material.BEDROCK);
    }

    @Override
    public void additionalEffect(Player player, Location location) {
        player.spawnParticle(Particle.ASH, location.add(0.0d, 0.25d, 0.0d), 3, 0.2d, 0.0d, 0.2d, 0);
    }
}
