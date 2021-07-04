package ru.hapyl.classesfight.disaster.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.disaster.Disaster;

public class TheDarkNight extends Disaster {

	private boolean strike;

	public TheDarkNight(Material material, String name, String about) {
		super(material, name, about);
	}

	@Override
	public void onStart() {
		resetTick();
	}

	@Override
	public void onStop() {
	}

	private void resetTick() {
		this.currentTick = 0;
		this.strike = false;
	}

	@Override
	public void onTick() {
		++currentTick;
		if (currentTick >= 600) {
			if (!strike) {
				strike = true;
				manager.getPlayers().forEach(player -> {
					PlayerLib.removeEffect(player, PotionEffectType.BLINDNESS);
					player.getWorld().strikeLightningEffect(player.getLocation().add(0, 1, 0));
				});
			}
			if (currentTick >= 700) {
				resetTick();
			}
			return;
		}
		manager.getPlayers().forEach(player -> PlayerLib.addEffect(player, PotionEffectType.BLINDNESS, 21, 1));
	}

}
