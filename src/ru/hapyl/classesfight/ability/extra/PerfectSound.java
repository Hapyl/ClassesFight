package ru.hapyl.classesfight.ability.extra;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public record PerfectSound(Sound sound, float pitch) {

	public void play(Location location) {
		PlayerLib.playSound(location, this.sound, this.pitch);
	}

	public void play(Player player) {
		PlayerLib.playSound(player, this.sound, this.pitch);
	}

	public static PerfectSound ps(Sound sound, float pitch) {
		return new PerfectSound(sound, pitch);
	}

}
