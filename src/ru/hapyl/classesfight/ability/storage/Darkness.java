package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.ability.extra.GrimoireCooldownApplier;
import ru.hapyl.classesfight.classes.iclass.extra.GrimmoreBook;
import ru.hapyl.classesfight.classes.iclass.inst.Instance;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.utils.GameUtils;

public class Darkness extends Ability implements GrimoireCooldownApplier {
	public Darkness() {
		super("Darkness Spell", "");
		this.setItem(Material.WITHER_ROSE);
		this.getItem().setStartAmount(3);
	}

	@Override
	public Response useAbility(Player player) {
		PlayerLib.playSound(player.getLocation(), Sound.ENTITY_SQUID_SQUIRT, 1.25f);
		final Location location = player.getLocation().add(0.0d, 0.5d, 0.0d);
		final Vector direction = location.getDirection();
		final double step = 0.25d;
		for (double i = 0.0d; i < 10.0d; i += step) {
			final double x = direction.getX() * i;
			final double y = direction.getY() * i;
			final double z = direction.getZ() * i;

			location.add(x, y, z);
			if (location.getBlock().getType().isOccluding()) {
				PlayerLib.playSound(location, Sound.BLOCK_STONE_STEP, 0.0f);
				break;
			}

			PlayerLib.spawnParticle(location, Particle.SQUID_INK, 1, 0.1, 0.05, 0.1, 0);
			GameUtils.getPlayerInRange(location, 1.0d).forEach(target -> {
				if (target == player) {
					return;
				}
				PlayerLib.playSound(location, Sound.BLOCK_STONE_STEP, 0.0f);
				GameEffectManager.applyEffect(target, GameEffectType.PARANOIA, 10);
				DamageFeature.damage(target, player, Instance.LIBRARIAN.calculateDamage(player, 2.0d), EnumDamageCause.DARKNESS);
			});

			//location.subtract(x, y, z);

		}

		Instance.LIBRARIAN.removeSpellItems(player, Abilities.ENTITY_DARKNESS);
		GrimmoreBook.applyCooldown(player, getGrimmoreCooldown());
		return Response.OK;
	}

	@Override
	public int getGrimmoreCooldown() {
		return 25 * 20;
	}
}
