package ru.hapyl.classesfight.gameeffect;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HealthShield extends VarArgsGameEffect<Double> {

	@Override
	public void onEffectStarts(Player player, Double d) {
		// FIXME: 030. 06/30/2021 -> does not reset when applying when already applied
		setValue(player, d);
	}

	@Override
	public void onEffectStop(Player player) {
		removeValue(player);
		PlayerLib.playSound(player, Sound.BLOCK_GLASS_BREAK, 0.0f);
	}

	@Override
	public String getEffectName() {
		return "Shielded";
	}

	@Override
	public String getExtraInfo(Player player) {
		return BukkitUtils.decimalFormat(getValue(player)) + "🛡";
	}

	@Override
	protected Double defaultValue() {
		return 0.0d;
	}
}
