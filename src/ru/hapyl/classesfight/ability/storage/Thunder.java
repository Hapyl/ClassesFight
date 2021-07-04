package ru.hapyl.classesfight.ability.storage;

import com.google.common.collect.Maps;
import kz.hapyl.spigotutils.module.block.BlockMagic;
import kz.hapyl.spigotutils.module.math.IntInt;
import kz.hapyl.spigotutils.module.math.Numbers;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.ThreadRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.disaster.Disaster;
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.GameUtils;

import java.util.Map;

public class Thunder extends Disaster {

	private int nextStrike = 0;
	private final Map<Location, BlockMagic> locations;

	public Thunder(Material material, String name, String about) {
		super(material, name, about);
		this.locations = Maps.newHashMap();
	}

	@Override
	public void onTick() {
		++currentTick;
		if (currentTick >= nextStrike) {
			resetCounter();
			manager.getPlayers().forEach(player -> locations.put(player.getLocation(), createBlob(player)));
			final IntInt i = new IntInt(0);
			GameTask.runTaskTimerTimes((task, tick) -> {
				if (tick % 3 == 0) {
					// fx
					if (tick != 0) {
						locations.forEach((loc, block) -> PlayerLib.playSound(loc, Sound.ENTITY_IRON_GOLEM_HURT, Numbers.clamp(1.0f + (0.1f * i.getAndAdd(1)), 1.0f, 2.0f)));
					}
					else {
						strike();
					}
				}
			}, 1, 30);
		}
	}

	private void strike() {
		locations.forEach((loc, block) -> {
			if (loc.getWorld() != null) {
				loc.getWorld().strikeLightningEffect(loc);
			}
			GameUtils.getPlayerInRange(loc, 1.5d).forEach(player -> DamageFeature.damage(player, null, 10.0d, EnumDamageCause.MAGIC));
		});
		locations.values().forEach(BlockMagic::reset);
	}

	private BlockMagic createBlob(Player player) {
		final Location cloned = player.getLocation().subtract(1, 1, 1);
		final BlockMagic blockMagic = new BlockMagic();
		for (int x = 0; x < 3; x++) {
			for (int z = 0; z < 3; z++) {
				if (x % 2 == 0 && z != 1) {
					continue;
				}
				cloned.add(x, 0, z);
				if (!cloned.getBlock().getType().isAir()) {
					blockMagic.addBlock(cloned.getBlock());
				}
				cloned.subtract(x, 0, z);
			}
		}
		blockMagic.sendChange(Material.IRON_BLOCK);
		return blockMagic;
	}

	private void resetCounter() {
		this.locations.clear();
		this.currentTick = 0;
		this.nextStrike = ThreadRandom.nextInt(10 * 20, 11 * 20); // 400, 700
	}

	@Override
	public void onStart() {
		resetCounter();
	}

	@Override
	public void onStop() {

	}
}
