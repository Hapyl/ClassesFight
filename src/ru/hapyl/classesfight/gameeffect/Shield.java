package ru.hapyl.classesfight.gameeffect;

import org.bukkit.entity.Player;

// Should probably use T but...
public class Shield extends VarArgsGameEffect<Integer> {

	@Override
	protected Integer defaultValue() {
		return 0;
	}

	@Override
	public void onEffectStarts(Player player, Integer integer) {
		setValue(player, integer);
	}

	@Override
	public void onEffectStop(Player player) {
		removeValue(player);
	}

	@Override
	public String getExtraInfo(Player player) {
		final int charges = getValue(player);
		return (charges == -1 ? "∞" : charges) + "";
	}

	@Override
	public String getEffectName() {
		return "Shielded";
	}
}
