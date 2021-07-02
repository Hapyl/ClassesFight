package ru.hapyl.classesfight.classes.iclass;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassEquipment;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.Weapon;

public class WindWarrior extends IClass {

	public WindWarrior() {
		super("Wind Warrior", Material.ELYTRA);
		this.setRole(ClassRole.ASSASSIN);
		this.setInfo("Light as a leaf and fast as a samurai. Uses his wind abilities to gain control over his enemies.",
				Abilities.WIND_STRIDER.getAbout(), "Wind Fury", "Instantly slash with your sword to create tornado and launch it forward. Enemies who get close to it will be pulled in and rapidly damaged.", 8);

		final ClassEquipment eq = this.getClassEquipment();

		eq.addItem(new Weapon(Material.LILY_PAD).withName("Clover").withDamage(3.0d).build());
		Abilities.WIND_STRIDER.addItemIfExists(eq);
	}

	@Override
	public void onPlayerRevealed(Player player) {

	}

	@Override
	public void useUltimate(Player player) {
		final Location startLocation = player.getLocation().add(player.getLocation().getDirection().multiply(2.5d).setY(0.0d));

		GameUtils.rayTraceLine(startLocation, 30, 0.25, 1, move -> {
			final Location location = move.getLocation();
			final double tT = move.getTick() * 2.0d;
			for (double y = 0.0d; y < 5.0; y += 0.5d) {
				double yM = y / 2.0d;
				for (double t = 0.0d; t < Math.PI * (yM + 0.5d); t += Math.PI / 8) {
					final double x = yM * Math.sin(t + tT);
					final double z = yM * Math.cos(t + tT);
					location.add(x, y, z);
					PlayerLib.spawnParticle(location, Particle.CRIT, 1, 0, 0, 0, 0);
					location.subtract(x, y, z);
				}
			}
			GameUtils.getPlayerInRange(location, 2.0d).forEach(target -> {
				if (target == player) {
					return;
				}
				target.setVelocity(location.toVector().subtract(target.getLocation().toVector()).multiply(0.45d));
				if (move.getTick() % 2.5d == 0) {
					DamageFeature.damage(target, player, 4.5d, EnumDamageCause.ENTITY_ATTACK);
				}
			});
		}, null);

	}

}
