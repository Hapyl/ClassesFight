/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

package ru.hapyl.classesfight.parkour;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.experience.reward.Reward;
import ru.hapyl.classesfight.feature.BlockLocation;

import javax.annotation.Nullable;
import java.util.*;

public class Parkour {

	private static final BlockLocation defaultQuitPlace = new BlockLocation(0, 15, 0);

	private final String name;
	private final ParkourBlock start;
	private final ParkourBlock finish;
	private final Map<Long, Reward> completionRewards;

	private Material icon;

	private ParkourDifficulty difficulty;
	private BlockLocation quitPlace, leadersLocation;
	private List<ParkourBlock> checkpoints;
	private int coinsReward;

	public Parkour(String name, ParkourBlock start, ParkourBlock finish) {
		this.name = name;
		this.start = start;
		this.finish = finish;
		this.coinsReward = 0;
		this.checkpoints = new ArrayList<>();
		this.quitPlace = defaultQuitPlace;
		this.difficulty = ParkourDifficulty.NORMAL;
		this.leadersLocation = null;
		this.completionRewards = new HashMap<>();
		this.icon = Material.RABBIT_HIDE;
	}

	public void setIcon(Material icon) {
		this.icon = icon;
	}

	public Material getIcon() {
		return icon;
	}

	public void setCompletionRewards(long time, Reward reward) {
		this.completionRewards.put(time, reward);
	}

	public Map<Long, Reward> getCompletionRewards() {
		return completionRewards;
	}

	public boolean hasCompletionRewards() {
		return !this.completionRewards.isEmpty();
	}

	protected void setDifficulty(ParkourDifficulty difficulty) {
		this.difficulty = difficulty;
	}

	protected void setLeadersLocation(BlockLocation location) {
		this.leadersLocation = location;
	}

	public ParkourDifficulty getDifficulty() {
		return difficulty;
	}

	@Nullable
	public BlockLocation getLeadersLocation() {
		return leadersLocation;
	}

	protected void setQuitPlace(BlockLocation quitPlace) {
		this.quitPlace = quitPlace;
	}

	public BlockLocation getQuitPlace() {
		return this.quitPlace;
	}

	protected void setCoinsReward(int coins) {
		this.coinsReward = coins;
	}

	protected void addCheckpoint(ParkourBlock location) {
		this.checkpoints.add(location);
	}

	protected void setCheckpoints(List<ParkourBlock> newList) {
		this.checkpoints = newList;
	}

	public final List<ParkourBlock> getCheckpoints() {
		return this.checkpoints;
	}

	public void spawnBlocks(World world) {
		this.start.spawn(world);
		this.finish.spawn(world);
		for (ParkourBlock checkpoint : this.checkpoints) {
			checkpoint.spawn(world);
		}
	}

	public void destroyBlocks(World world) {
		this.start.remove(world);
		this.finish.remove(world);
		for (ParkourBlock checkpoint : this.checkpoints) {
			checkpoint.remove(world);
		}
	}

	public final BlockLocation getStart() {
		return this.start;
	}

	public boolean hasCompleteBefore(Player player) {
		return Database.getDatabase(player).getBooleanEntry().getBoolean(this.getDatabasePath(), false);
	}

	public void setCompletion(Player player, boolean bool) {
		Database.getDatabase(player).getBooleanEntry().setBoolean(this.getDatabasePath(), bool);
	}

	public String getName() {
		return this.toString();
	}

	public String getDatabasePath() {
		return this.name.replace(" ", "").toLowerCase();
	}

	public final BlockLocation getFinish() {
		return this.finish;
	}

	public boolean hasCoinsReward() {
		return this.getCoinsReward() > 0;
	}

	public int getCoinsReward() {
		return this.coinsReward;
	}

	protected static ParkourBlock lazyCheckpoint(int x, int y, int z, float yaw, float pitch) {
		return new ParkourBlock(ParkourBlockType.CHECKPOINT, x, y, z, yaw, pitch);
	}

	@Override
	public String toString() {
		return this.name + " Parkour";
	}

	public String printDebugData(Player player) {
		return "Parkour{" + "name='" + name + '\'' +
				", start=" + start +
				", finish=" + finish +
				", quitPlace=" + quitPlace +
				", checkpoints=" + checkpoints +
				", player=" + player.getName() +
				", playerPos=" + player.getLocation() +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Parkour parkour = (Parkour)o;
		return coinsReward == parkour.coinsReward &&
				Objects.equals(name, parkour.name) &&
				Objects.equals(start, parkour.start) &&
				Objects.equals(finish, parkour.finish) &&
				Objects.equals(quitPlace, parkour.quitPlace) &&
				Objects.equals(checkpoints, parkour.checkpoints);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, start, finish, quitPlace, checkpoints, coinsReward);
	}
}
