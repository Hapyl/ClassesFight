package ru.hapyl.classesfight.feature;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.cooldown.Cooldown;

import java.util.function.Consumer;

public class AbilitiesCooldown {

	public static void add(final Player player, String name, String cdKey, int ticks, Consumer<Player> finish) {
		startCooldown(player, name, cdKey, ticks, finish);
	}

	public static void add(final Player player, final String name, final String cdKey, final int ticks, final ItemStack returnItem) {
		add(player, name, cdKey, ticks, g -> g.getInventory().addItem(returnItem));
	}

	public static void add(final Player player, String name, String cdKey, int ticks) {
		add(player, name, cdKey, ticks, (Consumer<Player>)null);
	}

	public static void startCooldown(Player player, String name, String id, int tick, Consumer<Player> whenFinished) {
		if (GameManager.current().isDebugMode()) {
			whenFinished.accept(player);
			return;
		}
		new Cooldown(player, id, name, tick) {
			@Override
			public void onStop() {
				whenFinished.accept(player);
			}
		};
	}

}
