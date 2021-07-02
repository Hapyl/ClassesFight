package ru.hapyl.classesfight.gameeffect;

import com.google.common.collect.Maps;
import org.bukkit.entity.Player;

import java.util.Map;

public abstract class VarArgsGameEffect<T extends Number> implements GameEffect {

	protected final Map<Player, T> values;

	protected VarArgsGameEffect() {
		this.values = Maps.newHashMap();
	}

	public abstract void onEffectStarts(Player player, T t);

	@Override
	public final void onEffectStart(Player player, Object... extra) {
		this.onEffectStarts(player, (T)extra[0]);
	}

	public void setValue(Player player, T t) {
		values.put(player, t);
	}

	public T getValue(Player player) {
		return values.getOrDefault(player, defaultValue());
	}

	public void removeValue(Player player) {
		values.remove(player);
	}

	public final void clearValues() {
		values.clear();
	}

	protected abstract T defaultValue();
}
