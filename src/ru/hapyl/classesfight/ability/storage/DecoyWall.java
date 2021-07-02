package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.block.BlockMagic;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ability.Ability;
import ru.hapyl.classesfight.ability.Response;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.utils.Yaw;

public class DecoyWall extends Ability {
	private final BlockData[] materials = {
			Material.STONE_BRICKS.createBlockData(),
			Material.CRACKED_STONE_BRICKS.createBlockData(),
			Material.MOSSY_STONE_BRICKS.createBlockData(),
			Material.CHISELED_STONE_BRICKS.createBlockData()
	};

	public DecoyWall() {
		super("Passable", "Create a sturdy wall that is invisible and passable for you, everyone else would have to work to get through it.");
		this.setItem(Material.STONE_BRICKS);
		this.setCooldownSec(45);
	}

	@Override
	public Response useAbility(Player player) {
		final Block targetBlock = player.getTargetBlockExact(5);

		if (targetBlock == null) {
			return Response.error("No valid block in sight!");
		}

		final Location center = targetBlock.getRelative(BlockFace.UP).getLocation().add(0.5d, 0.0d, 0.5d);
		final Yaw direction = Yaw.getDirection(player);
		final boolean eastToWest = !(direction == Yaw.EAST || direction == Yaw.WEST);

		final BlockMagic magic = new BlockMagic();
		center.add(eastToWest ? 3 : 0, 0, !eastToWest ? 3 : 0);

		for (int y = 0; y < 3; y++) {
			for (int i = 0; i <= 6; i++) {
				center.subtract(eastToWest ? i : 0, -y, !eastToWest ? i : 0);
				final Block block = center.getBlock();
				if (block.getType().isAir()) {
					magic.addBlock(block);
				}
				center.add(eastToWest ? i : 0, -y, !eastToWest ? i : 0);
			}
		}

		// Create Wall
		magic.forEach(block -> {
			for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				if (onlinePlayer == player) {
					return;
				}
				onlinePlayer.sendBlockChange(block.getLocation(), CollectionUtils.randomElement(materials, materials[0]));
			}
		});

		// Display Particles for us since we can't see it
		GameTask.runTaskTimerTimes((task, tick) -> {
			magic.forEach(block -> {
				PlayerLib.spawnParticle(BukkitUtils.centerLocation(block.getLocation()), Particle.CLOUD, 1, 0, 0, 0, 0);
			});
			if (tick == 0) {
				magic.reset();
			}
		}, 20, 10);

		return Response.OK;
	}
}
