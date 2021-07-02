package ru.hapyl.classesfight.classes.iclass;

import com.google.common.collect.Maps;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.math.Geometry;
import kz.hapyl.spigotutils.module.math.gometry.Quality;
import kz.hapyl.spigotutils.module.math.gometry.WorldParticle;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.Weapon;
import ru.hapyl.classesfight.utils.entity.Entities;

import javax.annotation.Nullable;
import java.util.Map;

public class HocusClass extends IClass implements Listener {

	private final Map<Rabbit, Player> loyalRabbit = Maps.newHashMap();

	public HocusClass() {
		super("Hocus", Material.AMETHYST_SHARD);

		final ClassEquipment eq = this.getClassEquipment(true);

		eq.addItem(new Weapon(Material.KNOWLEDGE_BOOK, "hocus_weapon") {

			private final double radius = 2.5d;

			@Override
			public void onLeftClick(Player player) {
				final Location location = player.getLocation().add(player.getLocation().getDirection().setY(0.0d).multiply(4.0d));
				Geometry.drawCircle(location, radius, Quality.NORMAL, new WorldParticle(Particle.FLAME));
				GameUtils.getPlayerInRange(location, radius)
						.forEach(target -> DamageFeature.damage(target, player, 6.0d, EnumDamageCause.ENTITY_ATTACK));
				this.applyCooldown(player, 100);
			}

			@Override
			public void onRightClick(Player player) {
				GameUtils.rayTraceLine(player, 20, 1.0d, 1,
						move -> {
							move.stopIfNotPassable();
							PlayerLib.spawnParticle(move.getLocation(), Particle.FLAME, 1, 0, 0, 0, 0);
						}, hit -> {
							if (hit == player) {
								return;
							}
							DamageFeature.damage(hit, player, 2.0d, EnumDamageCause.ENTITY_ATTACK);
						});
				this.applyCooldown(player, 45);
			}

		}.withName("Catalyst")
				.withSmartLore("&eNormal Shot &6&lRIGHT CLICK__Shoot a blast of magic in front of you.____&eCharged Shot &6&lLEFT CLICK__&7Shoot a more powerful blast at the ground in front of you, dealing AoE damage in small radius.")
				.build());

		this.setInfo("A boy who mastered magic tricks, is he even here?__",
				Abilities.DECOY.getAbout() + Abilities.DECOY_WALL.getAbout(),
				"Killer Rabbit", "Summon loyal Killer Rabbit for &b20s &7who hunts nearby enemies.", 7);
		this.setUltimateSound(Sound.ENTITY_RABBIT_DEATH, 0.0f);
		this.setRole(ClassRole.STRATEGIST);

		Abilities.DECOY.addItemIfExists(eq);
		Abilities.DECOY_WALL.addItemIfExists(eq);
	}

	@Override
	public void onStopOnce() {
		loyalRabbit.keySet().forEach(Entity::remove);
		loyalRabbit.clear();
	}

	@Override
	public void useUltimate(Player player) {
		this.setUsingUltimate(player, true, 400);
		Chat.broadcast("&cKiller Rabbit: &fI'm here for vengeance!");

		final Rabbit rabbit = (Rabbit)Entities.RABBIT.spawn(player.getLocation(), me -> {
			me.setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
			me.setMaxHealth(2047.0d);
			me.setHealth(2047.0d);
			me.setCustomName(Chat.format("%s's Pet", player.getName()));
			me.setCustomNameVisible(true);
			loyalRabbit.put(me, player);
		});

		GameTask.runTaskTimerTimes((task, tick) -> {
			if (rabbit.getTarget() == null) {
				setNewTarget(rabbit);
			}
			if (tick == 0) {
				Chat.broadcast("&cKiller Rabbit: &fBye bye~");
				rabbit.remove();
				loyalRabbit.remove(rabbit);
			}
		}, 20, 20);

	}

	private void setNewTarget(Rabbit rabbit) {
		final Player target = findNearestTarget(rabbit);
		rabbit.setTarget(target);
		Bukkit.getPluginManager().callEvent(new EntityTargetLivingEntityEvent(rabbit, target, EntityTargetEvent.TargetReason.CLOSEST_PLAYER));
	}

	@EventHandler()
	public void handleKillerRabbitAI(EntityTargetEvent ev) {
		if (!GameManager.current().isGameInProgress()) {
			return;
		}
		final Entity entity = ev.getEntity();
		if (entity instanceof Rabbit rabbit && loyalRabbit.containsKey(rabbit)) {
			final Entity target = ev.getTarget();
			if (loyalRabbit.get(rabbit) == target) {
				ev.setCancelled(true);
				setNewTarget(rabbit);
			}
		}

	}

	@Nullable
	private Player findNearestTarget(Rabbit rabbit) {
		final Player owner = loyalRabbit.get(rabbit);
		final Location rabbitLocation = rabbit.getLocation();
		Player closest = null;
		double distance = 0.0d;
		for (final Player player : GameUtils.getPlayerInRange(rabbitLocation, 50)) {
			final double dist = player.getLocation().distance(rabbitLocation);
			if (player == owner) {
				continue;
			}
			if (closest == null) {
				closest = player;
				distance = dist;
			}
			else if (dist <= distance) {
				closest = player;
				distance = dist;
			}
		}
		return closest;
	}

}
