package ru.hapyl.classesfight.feature.record;

import kz.hapyl.spigotutils.module.annotate.NULLABLE;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.reflect.npc.HumanNPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.runnable.GameTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Record {

	public static final Map<UUID, Record> records = new HashMap<>();

	private final Player player;
	private final UUID uuid;
	private final Map<Long, RecordEntry> steps;

	private boolean finished;
	private boolean forceStop;

	private HumanNPC recordEntity;
	private GameTask task;

	public Record(Player player, boolean auto) {
		this.player = player;
		this.uuid = player.getUniqueId();
		this.steps = new HashMap<>();
		this.finished = false;
		this.startRecord();

		if (auto) {
			if (getRecord(player) != null) {
				final Record record = getRecord(player);
				record.forceStop();
			}
			records.put(player.getUniqueId(), this);
			sendRecordMessage("Started recording!");
		}

	}

	public Record(Player player) {
		this(player, true);
	}

	public static void clear(Player player) {
		final Record record = records.get(player.getUniqueId());
		if (record != null) {
			record.forceStop();
		}
		records.remove(player.getUniqueId());
		// cleared
	}

	public boolean isFinished() {
		return finished;
	}

	public Player getRecord() {
		return player;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public void startRecord() {
		new GameTask() {
			@Override
			public void run() {

				if (getMaxFrame() >= 6000) {
					setFinished(true);
					sendRecordMessage("&cStopped your record since it exceeded maximum amount of frames! (6000)");
				}

				if (isFinished() || forceStop) {
					this.cancel();
					return;
				}
				addStep(player);
			}
		}.runTaskTimer(0, 1);
	}

	public void forceStop() {
		this.forceStop = true;
		this.stopPlayingRecord();
		this.steps.clear();
	}

	public void addStep(Player player) {
		if (this.finished) {
			return;
		}
		this.steps.put((long)this.steps.size(), new RecordEntry(player));
	}

	public long getMaxFrame() {
		return this.steps.size();
	}

	public Map<Long, RecordEntry> getSteps() {
		return steps;
	}

	public void playReplay() {

		if (!this.finished) {
			return;
		}

		sendRecordMessage("&aStarted replay!");
		final Location location = this.steps.get(0L).getLocation();
		this.recordEntity = new HumanNPC(location, "&3[R] &f" + player.getName(), player.getName());
		this.recordEntity.showAll();

		this.task = new GameTask() {

			private long currentTick = 0;

			@Override
			public void run() {

				if (currentTick >= steps.size()) {
					stopPlayingRecord();
					sendRecordMessage("&aFinished playing replay!");
					this.cancel();
					return;
				}

				final RecordEntry nextEntry = steps.get(currentTick++);
				nextEntry.applyStates(recordEntity);
				recordEntity.syncText();

			}
		}.runTaskTimer(0, 1);
	}

	public void sendRecordMessage(String msg, Object... obj) {
		Chat.sendMessage(player, "&3[Record] &a" + msg, obj);
	}

	public void stopPlayingRecord() {
		if (this.recordEntity != null) {
			this.recordEntity.remove();
			this.recordEntity = null;
		}
		if (this.task != null) {
			this.task.cancel();
			this.task = null;
		}
	}

	@NULLABLE
	public static Record getRecord(Player player) {
		return records.getOrDefault(player.getUniqueId(), null);
	}

	public UUID getUuid() {
		return uuid;
	}

	public boolean isPlaying() {
		return this.recordEntity != null || this.task != null;
	}

	public void save() {
		records.put(this.getUuid(), this);
	}
}
