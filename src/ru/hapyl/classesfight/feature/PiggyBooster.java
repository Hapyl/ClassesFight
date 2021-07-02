package ru.hapyl.classesfight.feature;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.GameMap;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gameeffect.GameEffectType;
import ru.hapyl.classesfight.utils.SoundLib;

public class PiggyBooster {

	private final GameMap map;
	private final BlockLocation loc;
	private final Vector vec;

	public PiggyBooster(BlockLocation loc, Vector vec) {
		this(GameMap.SKY, loc, vec);
	}

	public PiggyBooster(GameMap map, BlockLocation loc, Vector vec) {
		this.map = map;
		this.loc = loc;
		this.vec = vec;
	}

	public GameMap getMap() {
		return map;
	}

	public PiggyBooster(int x, int y, int z, double vecX, double vecY, double vecZ) {
		this(new BlockLocation(x, y, z), new Vector(vecX, vecY, vecZ));
	}

	public PiggyBooster(GameMap map, int x, int y, int z, double vecX, double vecY, double vecZ) {
		this(map, new BlockLocation(x, y, z), new Vector(vecX, vecY, vecZ));
	}

	public Entity spawn(boolean debug) {
		final Location location = this.loc.centralize();
		Entity piggy = location.getWorld().spawn(location.add(0, 1, 0), ArmorStand.class, me -> {
			me.setSilent(true);
			me.setInvulnerable(true);
			me.setSmall(true);
			if (debug) {
				me.setCustomName(this.vec.toString());
				me.setCustomNameVisible(true);
			}
			else {
				me.setVisible(false);
			}
		});
		piggy.setVelocity(this.vec);
		return piggy;
	}

	public Entity spawnAndRide(Player player, boolean flag) {
		final Entity piggy = spawn(flag);
		piggy.addPassenger(player);
		GameEffectManager.applyEffect(player, GameEffectType.FALL_DAMAGE_RESISTANCE, 200);
		SoundLib.play(player, Sound.ENTITY_GENERIC_EXPLODE, 2);
		return piggy;
	}

	public BlockLocation getLocation() {
		return loc;
	}
}