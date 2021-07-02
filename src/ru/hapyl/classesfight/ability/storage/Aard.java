package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.utils.GameUtils;

public class Aard extends Ability {

	public Aard() {
		super("Aard", "Creates a small explosion in front of you that pushes enemies away.");
		this.setItem(Material.HEART_OF_THE_SEA);
		this.setCooldownSec(5);
	}

	@Override
	public Response useAbility(Player player) {

		final Vector vector = player.getLocation().getDirection().setY(0.125d).multiply(2.0d);
		final Location inFront = player.getLocation().add(vector);
		final World world = inFront.getWorld();

		if (world != null) {
			PlayerLib.playSound(inFront, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.25f);
			PlayerLib.spawnParticle(inFront, Particle.EXPLOSION_LARGE, 1, 0, 0, 0, 0);
			GameUtils.getLivingEntitiesInRange(inFront, 4.0d).forEach(entity -> {
				if (entity == player || Spectator.isSpectator((Player)entity)) {
					return;
				}
				entity.setVelocity(vector);
			});
		}

		return Response.OK;
	}
}
