package ru.hapyl.classesfight.ability;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.iclass.extra.PrincessCrown;

public class PrincessFlash extends Ability implements PrincessCrown {
	public PrincessFlash() {
		super("Shining Gold", "Toss your crown into the air. After a brief delay, shines it's brightest - blinds and damages everyone who was looking at it.");
		this.setItem(Material.GOLDEN_HELMET);
		this.setCooldownSec(16);
	}

	@Override
	public Response useAbility(Player player) {
		PlayerLib.playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GOLD, 1.0f);
		playCrownAnimation(player, 20, stand -> {
			PlayerLib.spawnParticle(stand.getLocation(), Particle.FLASH, 5, 0, 0, 0, 0);
			PlayerLib.playSound(stand.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, 0.5f);
			GameManager.current().getPlayers().forEach(target -> {
				if (target == player) {
					return;
				}
				if (target.hasLineOfSight(stand)) {
					PlayerLib.addEffect(target, PotionEffectType.BLINDNESS, 60, 1);
				}
			});
		});
		return Response.OK;
	}
}
