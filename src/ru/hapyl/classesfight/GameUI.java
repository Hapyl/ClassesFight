package ru.hapyl.classesfight;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.scoreboard.ScoreboardBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassUltimate;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.Setting;
import ru.hapyl.classesfight.experience.Experience;
import ru.hapyl.classesfight.experience.RewardReason;
import ru.hapyl.classesfight.feature.npc.NpcManager;
import ru.hapyl.classesfight.utils.GameElement;
import ru.hapyl.classesfight.utils.GameUtils;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class GameUI implements GameElement, Runnable {

	private final BossBar bossBar;
	private final Map<Player, ScoreboardBuilder> scoreboards;
	private final Map<Player, ActionbarUI> uis;

	private long startTime;
	private int gameUptime;
	private int gameFrame;

	private final String gameName;

	public GameUI(JavaPlugin plugin) {
		this.bossBar = Bukkit.createBossBar(getCurrentMapName(), BarColor.RED, BarStyle.SOLID);
		this.scoreboards = new HashMap<>();
		this.uis = new HashMap<>();
		this.gameName = "&e&lCLASSES FIGHT";
		plugin.getServer().getScheduler().runTaskTimer(plugin, this, 0, 1);
	}

	public void createScoreboard(Player player) {
		if (this.scoreboards.containsKey(player)) {
			Chat.sendMessage(player, "&cScoreboard Creation Rejected! &7Already created.");
			return;
		}
		final ScoreboardBuilder scoreboard = new ScoreboardBuilder("gameScore", gameName, getScoreboardLines());
		this.scoreboards.put(player, scoreboard);
		this.uis.put(player, new ActionbarUI(player));
		this.bossBar.addPlayer(player);
		this.updateScoreboard(player);
	}

	public void updateScoreboard(Player player) {

		final Database database = Database.getDatabase(player);
		final GameManager manager = GameManager.current();

		if (!Database.isDataLoaded(player) || manager.isGameInProgress() && Setting.HIDE_SCOREBOARD_IN_GAME.isEnabled(player)) {
			return;
		}

		final ScoreboardBuilder scoreboard = getScoreboard(player);

		// Should never be null since we create before put just Java is Java
		if (scoreboard == null) {
			return;
		}

		final ClassManager playerClass = ClassManager.getClass(player);
		final ClassUltimate classUltimate = playerClass.getUltimate();
		final int ultimatePointsRequired = ClassUltimate.getPointsNeeded(player);
		final int ultimatePlayerPoints = ClassUltimate.getPoints(player);

		if (manager.isGameInProgress()) {
			scoreboard.setLines(
					Chat.format("&8%s, %s", GameUtils.getTodayDate(), ClassesFight.getPluginVersion()),
					null,
					"&a&lGame",
					" &fCurrent Map &6" + GameMap.getCurrentMap().getMapName(),
					" &fAlive Players &6" + manager.getPlayers().size(),
					" &fTime Passed &6" + getTimeString(),
					null,
					" &fUltimate &6" + (playerClass.getTheClass().isUsingUltimate(player) ?
							"&a&lIN USE" :
							(ultimatePlayerPoints == ultimatePointsRequired ?
									getTime()[0] % 2 == 0 ? "&6&lREADY" : "&e&lREADY" :
									ultimatePlayerPoints + "/" + ultimatePointsRequired + (ClassUltimate.perkAffected(player) ?
											String.format(" &f(&l%s&f)", classUltimate.getPoints()) :
											""))),
					null,
					"&a&lYou, " + player.getName(),
					Chat.format(" &fClass &6%s", (database.getSettingEntry().isEnabled(Setting.ALWAYS_RANDOM_CLASS) ?
							"Random" :
							playerClass.getDisplayName())),
					Chat.format(" &fCoins &6" + String.format("%,d", database.getCoinsEntry().getCoins())),
					Chat.format(" &fRubies &6" + String.format("%,d", database.getRubyEntry().getRubies())),
					null,
					"&ediscord.me/honorelondo");

		}
		else {
			scoreboard.setLines(
					Chat.format("&8%s, %s", GameUtils.getTodayDate(), ClassesFight.getPluginVersion()),
					null,
					"&a&lGame",
					" &fState &6" + manager.getState().cuteName(),
					Chat.format(" &fPlayers &6%s&7/&6%s", Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers()),
					null,
					"&a&lYou, " + player.getName(),
					Chat.format(" &fClass &6%s", (database.getSettingEntry().isEnabled(Setting.ALWAYS_RANDOM_CLASS) ?
							"Random" :
							playerClass.getDisplayName())),
					Chat.format(" &fCoins &6" + String.format("%,d", database.getCoinsEntry().getCoins())),
					Chat.format(" &fRubies &6" + String.format("%,d", database.getRubyEntry().getRubies())),
					null,
					"&ediscord.me/honorelondo"
			);
		}

		scoreboard.showIfNotShown(player);

	}

	public ActionbarUI getActionbarUI(Player player) {
		return this.uis.get(player);
	}

	private void bossBarWorker(boolean flag) {
		this.bossBar.setTitle(getCurrentMapName());
		this.bossBar.setVisible(flag);

		if (flag) {
			Bukkit.getOnlinePlayers().forEach(this.bossBar::addPlayer);
		}
		else {
			this.bossBar.removeAll();
		}
	}

	@Override
	public void run() {
		++gameUptime;
		++gameFrame;

		// Each Second
		if ((gameUptime % 20) == 0) {
			this.updateScoreboards();
			// While in game
			if (GameManager.current().isGameInProgress()) {
				this.checkTime();
				GameManager.current().forEachInGamePlayer(player -> player.getInventory().remove(Material.GLASS_BOTTLE));
			}
			else {
				this.bossBarWorker(true);
				Bukkit.getOnlinePlayers().forEach(player -> {
					if (Setting.SPECTATE.isEnabled(player)) {
						Chat.sendActionbar(player, "&aYou will spectate when the game starts.");
					}
				});
			}
		}

		// Update Actionbars
		this.uis.values().forEach(ActionbarUI::tick);

		if (GameManager.current().isDebugMode()) {
			this.updateScoreboardTitle("&4&lDEBUGGING");
		}
		else {
			if (gameFrame >= 150) {
				gameFrame = 0;
			}

			switch (gameFrame) {
				case 50 -> this.updateScoreboardTitle("&c&lC&e&lLASSES FIGHT");
				case 52 -> this.updateScoreboardTitle("&6&lC&c&lL&e&lASSES FIGHT");
				case 54 -> this.updateScoreboardTitle("C&6&lL&c&lA&e&lSSES FIGHT");
				case 56 -> this.updateScoreboardTitle("CL&6&lA&c&lS&e&lSES FIGHT");
				case 58 -> this.updateScoreboardTitle("CLA&6&lS&c&lS&e&lES FIGHT");
				case 60 -> this.updateScoreboardTitle("CLAS&6&lS&c&lE&e&lS FIGHT");
				case 62 -> this.updateScoreboardTitle("CLASS&6&lE&c&lS &e&lFIGHT");
				case 64 -> this.updateScoreboardTitle("CLASSE&6&lS &c&lF&e&lIGHT");
				case 66 -> this.updateScoreboardTitle("CLASSES &6&lF&c&lI&e&lGHT");
				case 68 -> this.updateScoreboardTitle("CLASSES F&6&lI&c&lG&e&lHT");
				case 70 -> this.updateScoreboardTitle("CLASSES FI&6&lG&c&lH&e&lT");
				case 72 -> this.updateScoreboardTitle("CLASSES FIG&6&lH&c&lT");
				case 74 -> this.updateScoreboardTitle("CLASSES FIGH&6&lT");
				case 76, 95 -> this.updateScoreboardTitle("CLASSES FIGHT");
				case 86, 92 -> this.updateScoreboardTitle("&6&lCLASSES FIGHT");
				case 89 -> this.updateScoreboardTitle("&c&lCLASSES FIGHT");
			}
		}

	}

	private void checkTime() {
		final int gameMaxTime = GameManager.current().getGameMode().getTimeLimit();
		final long[] times = getTime();
		long seconds = times[0];

		// Reward exp per minute played
		if (seconds % 60 == 0 && seconds < gameMaxTime && seconds > 1) {

			// why not just put time left here? -h
			sendInfo(getValidPronoun(gameMaxTime - seconds) + " remaining!");

			GameManager.current()
					.forEachInGamePlayer(player -> Experience.getInstance().rewardPlayer(player, true, RewardReason.MINUTE_PLAYED));
		}

		if (seconds >= gameMaxTime) {
			Bukkit.broadcastMessage(Chat.format("&2&lGAME OVER! &aTime is up, the game has over."));
			GameManager.current().stopGame();
		}
	}

	private String getValidPronoun(long seconds) {
		if (seconds < 60) {
			return seconds + " second" + (seconds == 1 ? "" : "s");
		}
		else if (seconds == 60) {
			return "1 minute";
		}
		else {
			return (seconds / 60) + " minutes";
		}
	}

	private void sendInfo(String info) {
		Bukkit.getOnlinePlayers().forEach(player -> {
			Chat.sendMessage(player, "&2&lTIME! &a" + info);
			PlayerLib.playSound(player, Sound.BLOCK_NOTE_BLOCK_HARP, 2.0f);
		});
	}

	public String getTimeString() {
		final DecimalFormat format = new DecimalFormat("00");
		return format.format(getTime()[1]) + ":" + format.format(getTime()[2]);
	}

	private long[] getTime() {
		return startTime <= 0 ? new long[]{0, 0, 0} : Chat.formatTime((System.currentTimeMillis() - startTime) / 1000);
	}

	public void updateScoreboardTitle(String title) {
		this.scoreboards.forEach((player, score) -> score.updateTitle(Chat.format("&e&l" + title)));
	}

	public void updateScoreboards() {
		this.scoreboards.keySet().forEach(this::updateScoreboard);
	}

	private int getScoreboardLines() {
		return GameManager.current().isGameInProgress() ? 14 : 11;
	}

	@Override
	public void onStart() {
		this.startTime = System.currentTimeMillis();
		this.bossBarWorker(false);
		this.scoreboards.forEach((player, score) -> {
			if (Setting.HIDE_SCOREBOARD_IN_GAME.isEnabled(player)) {
				score.hide(player);
				return;
			}
			score.setLines(getScoreboardLines());
			this.updateScoreboard(player);
		});
		// Hide names and add collision
		GameUtils.setNameTagVisibility(false);
	}

	@Override
	public void onStop() {
		this.startTime = -1;
		this.bossBarWorker(true);
		updateScoreboardTitle(this.gameName);
		this.scoreboards.forEach((player, score) -> {
			score.setLines(getScoreboardLines());
			this.updateScoreboard(player);
			NpcManager.getManager().updateGlobalNPCs(player);
		});
		// Show names
		GameUtils.setNameTagVisibility(true);
		this.uis.values().forEach(ActionbarUI::onStop);
	}

	@Nullable
	public ScoreboardBuilder getScoreboard(Player player) {
		if (!this.scoreboards.containsKey(player)) {
			this.createScoreboard(player);
		}
		return this.scoreboards.getOrDefault(player, null);
	}

	protected void resetAll() {
		bossBar.removeAll();
	}

	private String getCurrentMapName() {
		return Chat.format("&6&lCurrent Map: &e" + GameManager.current().getCurrentMapName());
	}

	public long getTimePlayed() {
		return this.getTime()[0];
	}
}
