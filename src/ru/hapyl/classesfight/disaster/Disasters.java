package ru.hapyl.classesfight.disaster;

import kz.hapyl.spigotutils.module.util.Action;
import org.bukkit.Material;
import ru.hapyl.classesfight.ability.storage.LowGravity;
import ru.hapyl.classesfight.ability.storage.Thunder;
import ru.hapyl.classesfight.disaster.storage.TheDarkNight;

public enum Disasters {

	THE_DARK_NIGHT(new TheDarkNight(Material.SQUID_SPAWN_EGG, "The Dark Night", "This is a very dark night, and only lightning can light the way.")),
	LOW_GRAVITY(new LowGravity(Material.END_STONE, "Low Gravity", "Are we going to moon?")),
	GOD_OF_THUNDER(new Thunder(Material.PUFFERFISH_BUCKET, "God of Thunder", "A lightning will charge periodically upon all players, dodge or suffer damage.")),
	;

	private final Disaster disaster;

	Disasters(Disaster disaster) {
		this.disaster = disaster;
	}

	public boolean isEnabled() {
		return disaster.isEnabled();
	}

	public void setEnabled(boolean flag) {
		disaster.setEnabled(flag);
	}

	public Disaster getDisaster() {
		return disaster;
	}

	public static void forEach(Action<Disaster> action) {
		for (final Disasters value : values()) {
			action.use(value.disaster);
		}
	}

}
