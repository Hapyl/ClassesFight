package ru.hapyl.classesfight.consumable.consume;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameMap;
import ru.hapyl.classesfight.consumable.Consumable;
import ru.hapyl.classesfight.feature.DamageFeature;

public class Burger extends Consumable {

	public Burger() {
		super("Burged", "Instantly eat a delicious Burger and gain &c2 ❤&7.", 300, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTUwMzkxYjEzODNkOWVmOWU2ZTdiZjVmODJkMWNmODgyZDgxZTJiMDA0ZGUzNjlmNzI5ZWRhMDRiMmVhNjNkMyJ9fX0=");
		this.addSpawnLocation(GameMap.ARENA, -100, 5, -105);
		this.addSpawnLocation(GameMap.GREENHOUSE, 6, 8, 67);
		this.addSpawnLocation(GameMap.NETHER, -155, 4, -62);
		this.addSpawnLocation(GameMap.JAPAN, 100, 17, -134);
		this.addSpawnLocation(GameMap.RAILWAY, 113, 9, 12);
		this.addSpawnLocation(GameMap.SKY, 1040, 26, 1008);
	}

	@Override
	public void consume(Player player) {
		DamageFeature.addHealth(player, 4.0f);
	}
}
