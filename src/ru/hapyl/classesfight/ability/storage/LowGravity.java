package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.disaster.Disaster;

public class LowGravity extends Disaster {
	public LowGravity(Material material, String name, String about) {
		super(material, name, about);
	}

	@Override
	public void onTick() {
		manager.getPlayers().forEach(player -> {
			PlayerLib.addEffect(player, PotionEffectType.JUMP, 20, 3);
			PlayerLib.addEffect(player, PotionEffectType.SLOW_FALLING, 20, 1);
		});
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}
}
