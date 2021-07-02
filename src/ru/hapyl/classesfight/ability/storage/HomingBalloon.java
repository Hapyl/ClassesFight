package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GarbageCollector;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.feature.Spectator;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

public class HomingBalloon extends Ability {

	public HomingBalloon() {
		super("Balloon Surprise", "Launches a water balloon that track nearest player and follows them for &b20s&7. If balloon hits its target or after &b20s&7, it explodes and applies drowning for &b3&7 second in small AoE and blinding enemies caught in range.");
		this.setCooldownSec(40);
		this.setItem(Material.HEART_OF_THE_SEA);
	}

	@Override
	public Response useAbility(Player player) {

		final Entity target = GameUtils.findNearestEntity(player.getLocation(), 50, player, EntityType.PLAYER);

		if (target == null) {
			return Response.error("No target found.");
		}

		Chat.sendTitle((Player)target, "&cCaution", "&7You're being targeted!", 10, 30, 10);

		final Guardian balloon = GarbageCollector.spawnEntity(player.getEyeLocation().clone().add(0.0d, 1.0d, 0.0d), Guardian.class, me -> {
			me.setSilent(true);
			me.setAware(false);
		});

		new GameTask() {
			private int currentTick = 0;

			@Override
			public void run() {

				if (target.isDead() || Spectator.isSpectator((Player)target)) {
					explode(balloon);
					this.cancel();
					return;
				}

				if (currentTick++ >= (20 * 20)) {
					explode(balloon);
					this.cancel();
					return;
				}

				// hit test
				final Entity entity = GameUtils.findNearestEntity(balloon.getEyeLocation(), 0.75d, balloon, EntityType.PLAYER);
				if ((entity != null) && (entity != player)) {
					explode(balloon);
					this.cancel();
					return;
				}

				// vector calculation
				final Vector vector = target.getLocation()
						.clone()
						.add(0d, 0.5d, 0.0d)
						.toVector()
						.subtract(balloon.getLocation().toVector())
						.normalize()
						.multiply(0.35);
				balloon.setVelocity(vector);

			}
		}.runTaskTimer(0, 1);

		return Response.OK;
	}

	private void explode(Guardian balloon) {
		final Location location = balloon.getLocation();
		float captureRadius = 3.0f;
		Geometry.drawSphere(location, 10.0d, captureRadius, new WorldParticle(Particle.WATER_SPLASH));
		if (location.getWorld() == null) {
			return;
		}
		balloon.getNearbyEntities(captureRadius, captureRadius, captureRadius).stream().filter(entity -> entity instanceof Player).forEach(entity -> {
			Player player = (Player)entity;
			player.spawnParticle(Particle.MOB_APPEARANCE, player.getLocation(), 1, 0, 0, 0);
			GameEffectManager.applyEffect(player, GameEffectType.DROWNING, 60);
		});
		balloon.remove();
		location.getWorld().playSound(location, Sound.ENTITY_GUARDIAN_FLOP, 20, 0.25f);
	}

}
