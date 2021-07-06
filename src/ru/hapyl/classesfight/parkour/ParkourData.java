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

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.feature.BlockLocation;
import ru.hapyl.classesfight.utils.OldPlayerLib;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ParkourData {

	private final Player player;
	private final Parkour parkour;

	private long startedAt;
	private long finishedAt = -1;
	private List<BlockLocation> checkPoints;
	private BlockLocation lastCheckpoint;

	// Stats
	private long jumps;
	private long checkpointTeleports;

	public ParkourData(Player player, Parkour parkour) {
		this.player = player;
		this.parkour = parkour;
		this.checkPoints = new ArrayList<>(parkour.getCheckpoints());
		this.startedAt = System.currentTimeMillis();
	}

	public void setFinished() {
		this.finishedAt = this.getTimePassed();
	}

	public long getTimePassed() {
		return System.currentTimeMillis() - this.startedAt;
	}

	public long getFinishedAt() {
		if (!this.isFinished()) {
			this.setFinished();
		}
		return finishedAt;
	}

	public Player getPlayer() {
		return player;
	}

	public void incrementJump() {
		++this.jumps;
	}

	public void incrementCheckpointsTeleports() {
		++this.checkpointTeleports;
	}

	public long getCheckpointTeleports() {
		return checkpointTeleports;
	}

	public long getJumps() {
		return jumps;
	}

	@Nullable
	public BlockLocation getNextCheckpoint() {
		return this.checkPoints.size() > 0 ? this.checkPoints.get(0) : null;
	}

	public boolean hasNextCheckpoint() {
		return this.getNextCheckpoint() != null;
	}

	public boolean isNextChechpoint(BlockLocation location) {
		if (!hasNextCheckpoint()) {
			return false;
		}
		else {
			return Objects.requireNonNull(getNextCheckpoint()).compare(location);
		}
	}

	public long getStartedAt() {
		return this.startedAt;
	}

	public boolean isFinished() {
		return this.finishedAt != -1;
	}

	public String getTimePassedFormatted() {
		final double timePassed = (double)(this.isFinished() ? this.getFinishedAt() : this.getTimePassed()) / 1000;
		final String format = new DecimalFormat("#0.00").format(timePassed);
//        return Chat.format("&6%s &f- &e%s", this.parkour.getName(), format);
		return Chat.format("&a&l%s &6&l%s", this.parkour.getName().toUpperCase(), format);
	}

	public void nextCheckpoint(boolean sendMessage) {
		if (hasNextCheckpoint()) {
			this.lastCheckpoint = this.getCurrentCheckpoint();
			this.checkPoints.remove(this.getCurrentCheckpoint());
			if (sendMessage) {
				OldPlayerLib.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f);
				ParkourManager.parkourMessage(player, String.format("Checkpoint passed! (%s/%s)",
						this.passedCheckpointCount(), this.parkour.getCheckpoints().size()));
			}
		}
	}

	public int passedCheckpointCount() {
		return (this.checkPoints.size() - this.parkour.getCheckpoints().size()) * -1;
	}

	public List<BlockLocation> getCheckPoints() {
		return this.checkPoints;
	}

	@Nullable
	public BlockLocation getCurrentCheckpoint() {
		return this.checkPoints.get(0);
	}

	public void resetTime() {
		this.startedAt = System.currentTimeMillis();
		this.jumps = 0;
		this.checkpointTeleports = 0;
	}

	@Nullable
	public BlockLocation getLastCheckpoint() {
		return this.lastCheckpoint;
	}

	public boolean hasLastCheckpoint() {
		return this.getLastCheckpoint() != null;
	}

	public void resetCheckpoints() {
		this.checkPoints = new ArrayList<>(this.parkour.getCheckpoints());
		this.lastCheckpoint = null;
	}

	public boolean prevCheckpoint(int[] blockLocation) {
		return this.lastCheckpoint != null && this.lastCheckpoint.compare(blockLocation);
	}

	public Parkour getParkour() {
		return this.parkour;
	}

	@Override
	public int hashCode() {
		int result = player != null ? player.hashCode() : 0;
		result = 31 * result + (parkour != null ? parkour.hashCode() : 0);
		result = 31 * result + (checkPoints != null ? checkPoints.hashCode() : 0);
		result = 31 * result + (int)(startedAt ^ (startedAt >>> 32));
		result = 31 * result + (lastCheckpoint != null ? lastCheckpoint.hashCode() : 0);
		return result;
	}
}
