package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Draw;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.utils.GameUtils;

public class WindStrider extends Ability {
	public WindStrider() {
		// Instantly create swirl of wind that pulls nearby enemies to it but pushes you up in the sky and grants you slow falling for a short duration.
		super("Wind Strider", "Instantly create swirl of wind at your current location. Use the gust of it to pull enemies and fly high up.");
		this.setItem(Material.FEATHER);
		this.setCooldownSec(11);
	}

	@Override
	public Response useAbility(Player player) {

		final Location location = player.getLocation();

		// fx
		PlayerLib.playSound(Sound.ITEM_TRIDENT_RIPTIDE_1, 0.75f);
		Geometry.drawCircle(location, 2.5d, Quality.NORMAL, new Draw(Particle.BLOCK_CRACK) {
			@Override
			public void draw(Location location) {
				final World world = location.getWorld();
				if (world == null) {
					return;
				}
				world.spawnParticle(this.getParticle(), location, 1, 0, 0, 0, 0, Material.FLOWERING_AZALEA_LEAVES.createBlockData());
			}
		});

		PlayerLib.addEffect(player, PotionEffectType.SLOW_FALLING, 40, 1);
		GameUtils.getPlayerInRange(location, 2.5d).forEach(target -> {
			if (target == player) {
				target.setVelocity(target.getVelocity().setY(1.25f));
			}
			else {
				target.setVelocity(location.toVector().subtract(target.getLocation().toVector()).multiply(0.6d));
			}
		});

		return Response.OK;
	}
}