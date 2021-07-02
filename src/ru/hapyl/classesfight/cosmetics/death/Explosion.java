package ru.hapyl.classesfight.cosmetics.death;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;

import java.util.Random;

public class Explosion extends AbstractEffect {

    public Explosion() {
        super("Big Blast", "Creates a firework explosion of random color.", 350, Material.FIREWORK_ROCKET, ShopItemRarity.RARE);
    }

    private final Color[] validColors = {Color.RED, Color.AQUA, Color.YELLOW, Color.GREEN, Color.ORANGE, Color.FUCHSIA, Color.LIME, Color.MAROON, Color.OLIVE, Color.PURPLE};

    @Override
    public void display(Player player) {

        final Location at = player.getLocation();
        final Firework firework = at.getWorld().spawn(at.add(0, 1, 0), Firework.class);

        Color theColor = validColors[new Random().nextInt(validColors.length)];

        final FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder().flicker(false).trail(false).withColor(theColor).with(FireworkEffect.Type.BALL).build());
        meta.setPower(0);
        firework.setFireworkMeta(meta);
        firework.detonate();

    }

}
