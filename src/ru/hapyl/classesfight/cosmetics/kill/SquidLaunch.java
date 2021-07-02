package ru.hapyl.classesfight.cosmetics.kill;

import com.google.common.util.concurrent.AtomicDouble;
import kz.hapyl.spigotutils.module.math.IntInt;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.cosmetics.AbstractEffect;
import ru.hapyl.classesfight.menu.collectibles.ShopItemRarity;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

public class SquidLaunch extends AbstractEffect {

    public SquidLaunch() {
        super("Squid Launch", "Launches a Squid to the space!__&8&oIs this the first one?", 500, Material.INK_SAC, ShopItemRarity.RARE);
    }

    @Override
    public void display(Player player) {

        final Location at = player.getLocation();
        Squid squid = at.getWorld().spawn(at, Squid.class);
        GameUtils.prepareEntity(squid);

        final AtomicDouble ad = new AtomicDouble(0.0d);
        GameTask.runTaskTimerTimes((task, tick) -> {
            PlayerLib.playSound(squid.getLocation(), Sound.ENTITY_CHICKEN_EGG, (float)(0.1f * ad.getAndAdd(0.5f)));
            squid.setVelocity(new Vector(0.0d, 0.7d, 0.0d));
            new WorldParticle(Particle.EXPLOSION_NORMAL, 3, 1, 0.1d, 0.1d, 0.1f).draw(squid.getLocation());
        }, 2, 10);

        new GameTask() {
            @Override
            public void run() {
                squid.remove();
            }
        }.runTaskAtCancel().runTaskLater(20);

    }

}
