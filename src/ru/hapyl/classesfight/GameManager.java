package ru.hapyl.classesfight;

import io.netty.util.internal.ConcurrentSet;
import kz.hapyl.spigotutils.module.annotate.NULLABLE;
import kz.hapyl.spigotutils.module.block.BlockMagic;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import kz.hapyl.spigotutils.module.util.CollectionUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.ability.Abilities;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassUltimate;
import ru.hapyl.classesfight.consumable.ConsumableManager;
import ru.hapyl.classesfight.cosmetics.EnumEffect;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.Setting;
import ru.hapyl.classesfight.database.entry.SettingEntry;
import ru.hapyl.classesfight.database.entry.StatType;
import ru.hapyl.classesfight.disaster.Disasters;
import ru.hapyl.classesfight.experience.Experience;
import ru.hapyl.classesfight.experience.RewardReason;
import ru.hapyl.classesfight.feature.*;
import ru.hapyl.classesfight.feature.japangod.JapanGod;
import ru.hapyl.classesfight.feature.perk.Perk;
import ru.hapyl.classesfight.gameeffect.GameEffectManager;
import ru.hapyl.classesfight.gamemode.GameModeEnum;
import ru.hapyl.classesfight.runnable.GameTask;
import ru.hapyl.classesfight.stats.StatsContainer;
import ru.hapyl.classesfight.utils.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static kz.hapyl.spigotutils.module.chat.Chat.format;
import static ru.hapyl.classesfight.classes.ClassManager.theFormat;

public class GameManager {

	public enum GameState {
		LOBBY,
		GAME,
		PAST_GAME,
		PRE_GAME;

		public String cuteName() {
			return WordUtils.capitalize(this.name().toLowerCase());
		}
	}

	private final Set<Player> playerStorage;
	private List<GameTeam> teamStorage;
	private Set<Player> hasBooster;
	private Map<Player, Integer> playerKills;
	/**
	 * Default boolean value is false
	 */
	private boolean playersRevealed;
	private boolean eventInProgress;
	private boolean eventPlayed;
	private boolean debugNoDelayMode;
	private boolean teamMode;
	private final TutorialBook tutorialBook;
	private Player mvpPlayer;
	private GameState gameState = GameState.LOBBY;
	private int timeBeforeReveal = 5;
	private GameModeEnum currentMode = GameModeEnum.NORMAL;

	private final GameSettings settings;

	private JapanGod japanGod;

	private final boolean ignorePlayerRequirement = true;

	public GameManager() {

		this.playerStorage = new ConcurrentSet<>();
		this.teamStorage = new ArrayList<>();
		this.hasBooster = new HashSet<>();
		this.playerKills = new HashMap<>();
		this.settings = new GameSettings();
		this.tutorialBook = new TutorialBook();

	}

	public GameSettings getSettings() {
		return settings;
	}

	public void startGame(boolean debugMode) {

		if (this.getState() == GameState.GAME) {
			errorMessage("Cannot start game. It's already in progress!");
			return;
		}

		if (GameMap.getCurrentMap() == GameMap.NONE) {
			errorMessage("Cannot start game. Map required to start the game!");
			return;
		}

		if (this.teamMode && GameTeam.getFilledTeams().size() < 2) {
			errorMessage("Cannot start game. Team mode enabled but there is only 1 team!");
			return;
		}

		if (ignorePlayerRequirement) {
			Bukkit.broadcastMessage(ChatColor.RED + "");
			Bukkit.broadcastMessage(ChatColor.RED + "[WARNING] Players requirement ignored.");
			Bukkit.broadcastMessage(ChatColor.RED + "[WARNING] Should not be implemented as final feature.");
			Bukkit.broadcastMessage(ChatColor.RED + "");
		}

		else {
			Set<Player> currentPlayers = Bukkit.getOnlinePlayers()
					.stream()
					.filter(player -> !Database.getDatabase(player).getSettingEntry().isEnabled(Setting.SPECTATE))
					.collect(Collectors.toSet());
			if (currentPlayers.size() < 2) {
				errorMessage("Cannot start game. Not enough players.");
				return;
			}
		}

		// *==* Can Start Game *==*
		potionDrinkSound.clearListeners();
		hasBooster.clear();

		if (debugMode) {
			this.debugNoDelayMode = true;
		}

		this.eventPlayed = false;

		// Reset Parkour
		ClassesFight.getSingleton().getParkourManager().resetAll();

		// Reworked Game Effects
		GameEffectManager.onStart();

		// Clear old tasks for Minigame
		GameTask.clearTasks();

		// Abilities onStart
		for (final Abilities value : Abilities.values()) {
			value.getAbility().onStart();
		}

		// Disasters
		Disasters.forEach(disaster -> {
			if (!disaster.isEnabled()) {
				return;
			}
			disaster.onStart();
			new GameTask() {
				@Override
				public void run() {
					disaster.onTick();
				}
			}.addCancelEvent(disaster::onStop).runTaskTimer(0, 1);
		});

		// this is the impl for now since fuck armor stands
		new HealthIndicator();

		if (GameMap.getCurrentMap() == GameMap.JAPAN) {
			japanGod = new JapanGod();
		}

		setState(GameState.PRE_GAME);
		ConsumableManager.current().spawnAll();

		teamStorage.addAll(GameTeam.getFilledTeams());

		this.getGameMode().processBeforeGameStart();

		// *--* Spawn Health Indicators *--*
		//this.indicators = new HealthIndicator();


		// *--* Player Iteration *--*
		Bukkit.getOnlinePlayers().iterator().forEachRemaining(player -> {
			final Database database = Database.getDatabase(player);
			final SettingEntry settings = Database.getDatabase(player).getSettingEntry();

			if (settings.isEnabled(Setting.SPECTATE)) {
				if (settings.isEnabled(Setting.SPECTATE)) {
					Spectator.add(player);
				}
				return;
			}

			if (settings.isEnabled(Setting.ALWAYS_RANDOM_CLASS)) {
				ClassManager randomClass = ClassManager.getRandomValidClass(player);
				ClassManager.setClass(player, randomClass, false);
				Chat.sendMessage(player, theFormat + "Selected &6%s &2class randomly since you have it enabled in settings!",
						randomClass.getDisplayName());
			}

			if (Setting.FAST_ABILITY_USE.isEnabled(player) && CFItemBuilderListener.isDisallowedClass(ClassManager.getClass(player))) {
				Chat.sendMessage(player, "&e&lTake Note!");
				Chat.sendMessage(player,
						"&aYour currently selected class does not support &lFast Ability Use &afeature! It has been disabled until you change class" +
								".");
			}

			player.setPlayerTime(settings.isEnabled(Setting.NIGHT_MODE) ? 18000 : 6000, false);

			// Check for the boosters and activate 'em
			if (database.getBoosterEntry().hasBooster(1)) {
				if (!settings.isEnabled(Setting.USE_BOOSTERS)) {
					Chat.sendMessage(player, "&3&lBOOSTER! &7Your potion haven't been used since it disabled for you!");
				}
				else {
					database.getBoosterEntry().removeBooster(1);
					Chat.sendMessage(player,
							"&3&lBOOSTER! &7You drank a Booster Potion! &6Coins &7and &aExperience &7you earn this game are &adoubled&7!");
					potionDrinkSound.addListener(player).play();
					hasBooster.add(player);
				}
			}

			/* Reset player before preparing for health */
			GameUtils.resetPlayer(player);

			/* Assign custom health */
			DamageFeature.preparePlayer(player);

			this.getPlayers().add(player);

			// Had to move this here
			ClassesFight.getSingleton().getGameUI().onStart();

			// New feature
			ClassUltimate.preparePlayer(player);

			// Check for perks
			if (Perk.SLITHERING.hasPerkAndEnabled(player)) {
				OldPlayerLib.addPotionEffect(player, PotionEffectType.SPEED, 61, 4);
				OldPlayerLib.playSound(player, Sound.ENTITY_CAT_HISS, 1.75f);
			}

			// Process Player Before Game Start
			this.getGameMode().processPlayerBeforeGameStart(player);

			if (!debugNoDelayMode) {

				playersRevealed = false;
				GameUtils.hideBut(player);
				player.sendMessage(format("&a&l➺ &aAll players have been hidden!"));
				player.sendMessage(format("&a&l➺ &aThey have &e" + timeBeforeReveal + "s &ato spread before being revealed."));

				new GameTask() {
					@Override
					public void run() {
						setState(GameState.GAME);
						playersRevealed = true;
						GameUtils.showBut(player);
						if (!settings.isEnabled(Setting.HIDE_START_FX)) {
							title(player);
							player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 1, 1.5f);
						}
						player.sendMessage(format("&a&l➺ &aPlayers have been revealed. &lFIGHT!"));
						player.getWorld().strikeLightningEffect(player.getLocation().clone().add(0, 1, 0));
					}
				}.runTaskLater(20L * timeBeforeReveal);
			}

			player.teleport(GameMap.getCurrentMap().getLocation());
			player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20, 250, true));

		});

		/*  */
		ClassesFight.getSingleton().getMapFeatures().onStart();

		// Teleport spectators as well
		for (Player player : Spectator.spectators) {
			player.teleport(GameMap.getCurrentMap().getLocation());
		}

		// Kills thing
		this.mvpPlayer = this.getRandomInGamePlayer();
		this.playerKills.clear();
		System.out.println("[DEBUG] Calling classes..");

		ClassManager.executeStartScriptForEach();

		if (debugNoDelayMode) {
			setState(GameState.GAME);
			playersRevealed = true;
			Bukkit.broadcastMessage(format("&aRunning in debug mode, skipping fade and ignoring cooldowns."));
			ClassManager.executeRevealedScriptForEach();
		}
		else {
			new GameTask() {
				@Override
				public void run() {
					ClassManager.executeRevealedScriptForEach();
				}
			}.runTaskLater(20L * this.timeBeforeReveal);
		}

	} // startGame()

	public void stopGame() {

		// Could fix death glitch? // no
		setState(GameState.LOBBY);
		this.debugNoDelayMode = false;

		// custom runnable cancel
		GameTask.clearTasks();

		DamageFeature.lastDamageCause.clear();
		ClassesFight.getSingleton().getMapFeatures().onStop();
		ClassUltimate.onStop();
		BlockMagic.resetAll();
		Spectator.wipe();
		ConsumableManager.current().removeAll();

		// reset ability cooldowns/onStop
		Abilities.resetAll();

		// GameEffect.removeAll();
		GameEffectManager.onStop();

		ClassManager.executeStopScriptForEach();
		GarbageCollector.onStop();

		// Effects
		for (EnumEffect value : EnumEffect.values()) {
			value.getClazz().onStop();
		}

		final GameUI gameUI = ClassesFight.getSingleton().getGameUI();

		final long totalTimePlayed = gameUI.getTimePlayed();
		gameUI.onStop();

		playerStorage.clear();
		teamStorage.clear();

		this.getGameMode().processAfterGameEnd();

		Bukkit.getOnlinePlayers().forEach(player -> {

			this.getGameMode().processPlayerAfterGameStart(player);
			player.setInvulnerable(false);

			if (totalTimePlayed > 0) {
				Database.getDatabase(player).getStatistics().addStat(StatType.TOTAL_TIME_PLAYED, (int)totalTimePlayed);
			}

			// Global Stat Controll
			ClassManager.getClass(player).addStat(StatsContainer.ClassStat.PLAYED_TIMES, 1L);

			GameUtils.resetPlayer(player);
			ConsumableManager.current().checkNotUsedConsumables(player);

			player.setVelocity(new Vector(0, 0, 0));
			player.teleport(GameMap.SPAWN.getLocation());
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();

			ClassesFight.getSingleton().getGameItems().giveAll(player);

			player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
			GameUtils.showBut(player);
		});


		final AutoSave autoSave = ClassesFight.getSingleton().getAutoSave();
		if (autoSave.shouldSaveAfterGame()) {
			autoSave.saveData();
		}

	}

	public void checkWin() {
		if (!getGameMode().checkCondition()) {
			return;
		}

		final GameUI ui = ClassesFight.getSingleton().getGameUI();
		final GameManager manager = GameManager.current();

		final String line = "&c&m[                                                                            ]";
		List<Player> winners;

		if (manager.getPlayers().isEmpty()) {
			Chat.broadcast("&cNo players in game? Stopping...");
			manager.stopGame();
			return;
		}

		setState(GameState.PAST_GAME);
		winners = Collections.singletonList(manager.getPlayer(0));
		// FIXME: 025. 02/25/2021 - Rework this for team mode
		final Player soloWinner = winners.get(0);

		// Display End Game information
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.sendMessage(Chat.format(line));
			CenterChat.sendCenteredMessage(player, "&eThe game has ended!");
			CenterChat.sendCenteredMessage(player, "&8" + ui.getTimeString());
			CenterChat.sendCenteredMessage(player, "&f");
			CenterChat.sendCenteredMessage(player, "&e&lWINNER");

			final Player winner = winners.get(0);
			CenterChat.sendCenteredMessage(player, String.format("%s &6%s &7(%s♥)", ClassManager.getClass(winner)
					.getDisplayName(), winner.getName(), formatHealth(winner)));

			final Map<Player, Integer> playerKills = manager.getPlayerKills();
			final Player mvpPlayer = manager.getMvpPlayer();

			CenterChat.sendCenteredMessage(player, "&f");
			CenterChat.sendCenteredMessage(player, "&c&lMOST KILLS");
			CenterChat.sendCenteredMessage(player, String.format("%s &6%s &e- &6%s",
					ClassManager.getClass(mvpPlayer).getDisplayName(), mvpPlayer.getName(),
					playerKills.getOrDefault(mvpPlayer, 0)));
			player.sendMessage(Chat.format(line));

		}

		Bukkit.getOnlinePlayers()
				.stream()
				.filter(player -> !winners.contains(player))
				.forEach(player -> {
					Chat.sendTitle(player, "&c&lDEFEAT", String.format("&e%s is the winner!", soloWinner.getName()), 10, 60, 5);
					final ClassManager playerClass = ClassManager.getClass(player);
					// Global Stats Info
					StatsContainer.current().addClassStat(playerClass, StatsContainer.ClassStat.LOSE, 1);
				});

		for (Player winner : winners) {
			winner.setInvulnerable(true);
			Experience.getInstance().rewardPlayer(winner, true, RewardReason.GAME_WON);
			final Database database = Database.getDatabase(winner);
			database.getCoinsEntry().grantCoins(coinsWin, true, "Game Won");

			final ClassManager playerClass = ClassManager.getClass(winner);
			// Global Stats Info
			StatsContainer.current().addClassStat(playerClass, StatsContainer.ClassStat.WIN, 1);

			database.getStatistics().addStat(StatType.WINS, 1);
			database.getStatistics().addClassWins(playerClass, 1);
			Chat.sendTitle(winner, "&6&lVICTORY", "&eYou're the winner!", 10, 60, 5);
			//spawnFireworks(winner.getLocation(), true);
		}

		this.spawnFireworks(soloWinner.getLocation(), true);

	}

	public final int coinsWin = 100;
	public final int coinsKill = 10;

	public int getCoinsKill() {
		return coinsKill;
	}

	private String formatHealth(Player player) {
		return BukkitUtils.decimalFormat(player.getHealth() / 2);
	}

	public void spawnFireworks(Location location, boolean flag) {

		final Set<Firework> fireworks = new HashSet<>();
		final int maxTimes = 18;
		final int delayPer = 5;

		new GameTask() {

			int currentTimes = 0;

			@Override
			public void run() {

				if (++currentTimes >= (maxTimes + 1)) {
					this.cancel();
				}
				else {
					final int randomX = new Random().nextInt(10);
					final int randomY = new Random().nextInt(5);
					final int randomZ = new Random().nextInt(10);

					final boolean negativeX = new Random().nextBoolean();
					final boolean negativeZ = new Random().nextBoolean();

					Location cloned = location.clone().add(negativeX ? -randomX : randomX, randomY, negativeZ ? -randomZ : randomZ);

					fireworks.add(cloned.getWorld().spawn(cloned, Firework.class, me -> {
						final FireworkMeta meta = me.getFireworkMeta();
						meta.setPower(2);
						//new FireworkEffect(true, true, getRandomColors(), getRandomColors(), FireworkEffect.Type.BURST))
						meta.addEffect(FireworkEffect.builder()
								.with(FireworkEffect.Type.BURST)
								.withColor(getRandomColor())
								.withFade(getRandomColor())
								.withTrail()
								.build());
						me.setFireworkMeta(meta);
					}));
				}

			}
		}.runTaskTimer(0, delayPer);

		if (flag) {
			GameTask.runLater(() -> {
				fireworks.forEach(Firework::remove);
				fireworks.clear();
				this.stopGame();
			}, (maxTimes * delayPer) + 20);
		}

	}

	private Color getRandomColor() {
		return Color.fromRGB(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));
	}

	@NULLABLE
	public JapanGod getJapanGod() {
		return japanGod;
	}

	/**
	 * Auto-Generated... Getters and Setters
	 */

	@Nullable
	public Player getPlayer(int index) {
		return index >= this.playerStorage.size() ? null : this.playerStorage.toArray(new Player[]{})[index];
	}

	public Set<Player> getPlayers() {
		return this.playerStorage;
	}

	public List<GameTeam> getTeamStorage() {
		return teamStorage;
	}

	public void setTeamStorage(List<GameTeam> teamStorage) {
		this.teamStorage = teamStorage;
	}

	public Set<Player> getHasBooster() {
		return hasBooster;
	}

	public void setHasBooster(Set<Player> hasBooster) {
		this.hasBooster = hasBooster;
	}

	public Map<Player, Integer> getPlayerKills() {
		return playerKills;
	}

	public void setPlayerKills(Map<Player, Integer> playerKills) {
		this.playerKills = playerKills;
	}

	public boolean isPlayersRevealed() {
		return playersRevealed;
	}

	public void setPlayersRevealed(boolean playersRevealed) {
		this.playersRevealed = playersRevealed;
	}

	public boolean isEventInProgress() {
		return eventInProgress;
	}

	public void setEventInProgress(boolean eventInProgress) {
		this.eventInProgress = eventInProgress;
	}

	public boolean isEventPlayed() {
		return eventPlayed;
	}

	public void setEventPlayed(boolean eventPlayed) {
		this.eventPlayed = eventPlayed;
	}

	public void setDebugNoDelayMode(boolean debugNoDelayMode) {
		this.debugNoDelayMode = debugNoDelayMode;
	}

	public boolean isTeamMode() {
		return teamMode;
	}

	public void setTeamMode(boolean teamMode) {
		this.teamMode = teamMode;
	}

	public TutorialBook getTutorialBook() {
		return tutorialBook;
	}

	public Player getMvpPlayer() {
		return mvpPlayer;
	}

	public void setMvpPlayer(Player mvpPlayer) {
		this.mvpPlayer = mvpPlayer;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public int getTimeBeforeReveal() {
		return timeBeforeReveal;
	}

	public void setTimeBeforeReveal(int timeBeforeReveal) {
		this.timeBeforeReveal = timeBeforeReveal;
	}

	public GameModeEnum getGameMode() {
		return currentMode;
	}

	public void setGameMode(GameModeEnum currentMode) {
		this.currentMode = currentMode;
	}

	public boolean isIgnorePlayerRequirement() {
		return ignorePlayerRequirement;
	}


	public boolean isDebugMode() {
		return this.debugNoDelayMode;
	}

	public boolean hasDrunkBooster(Player player) {
		return this.hasBooster.contains(player);
	}

	private static final ChainSound potionDrinkSound = new ChainSound(ClassesFight.getPlugin()).appendSameSound(Sound.ENTITY_GENERIC_DRINK, 1.8f, 0,
			2, 3, 2, 3).append(Sound.ENTITY_VILLAGER_YES, 1.5f, 0);

	public static GameManager current() {
		return ClassesFight.getSingleton().getGameManager();
	}

	public boolean isGameInProgress() {
		return this.gameState != GameState.LOBBY;
	}

	public void displayTutorial(Player player) {
		// if they clicked from menu
		player.closeInventory();
		player.openBook(this.tutorialBook.getItem());
	}

	public boolean arePlayersRevealed() {
		return playersRevealed;
	}

	public void errorMessage(String str, Object... abc) {
		Bukkit.broadcastMessage(format("&4&lERROR! &c" + str, abc));
	}

	//    @Deprecated
	//    private void newEventChance() {
	//
	//        final double maxChance = 15;
	//        GameScoreboard.eventChance = new Random().nextDouble() * maxChance;
	//        Bukkit.broadcastMessage(format("&b&lEVENT! &3This game has &9" + new DecimalFormat("##.##").format(GameScoreboard.eventChance) + "%
	//        &3chance for an Event to appear!"));
	//
	//        TextComponent text = new TextComponent(ChatColor.GRAY + "[Hover this text to get info about events]");
	//        text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(format("&7A random event may appear during the
	//        game.\n&7It has certain % chance to appear, that\n&7generates when game starts.\n\n" + "&eChance checking each second, if\n&eat least
	//        30 seconds passed.\n&7Only one event can appear in single game.")).create()));
	//
	//        Bukkit.spigot().broadcast(text);
	//        Bukkit.broadcastMessage(" ");
	//
	//    }

	// Shows the 'THE FIGHT BEGINS' animation to the player.
	private void title(Player player) {
		player.sendTitle(ChatColor.DARK_RED + "☠", format("&e&lTHE FIGHT BEGINS"), 9, 20, 0);
		new BukkitRunnable() {
			int frame = 0;

			@Override
			public void run() {

				frame++;
				switch (frame) {

					case 10:
						sendTitle(player, format("&c&lT&e&lHE FIGHT BEGINS"));
						break;
					case 12:
						sendTitle(player, format("&6&lT&c&lH&e&lE FIGHT BEGINS"));
						break;
					case 14:
						sendTitle(player, format("&e&lT&6&lH&c&lE &e&lFIGHT BEGINS"));
						break;
					case 16:
						sendTitle(player, format("&e&lTH&6&lE &c&lF&e&lIGHT BEGINS"));
						break;
					case 18:
						sendTitle(player, format("&e&lTHE &6&lF&c&lI&e&lGHT BEGINS"));
						break;
					case 20:
						sendTitle(player, format("&e&lTHE F&6&lI&c&lG&e&lHT BEGINS"));
						break;
					case 22:
						sendTitle(player, format("&e&lTHE FI&6&lG&c&lH&e&lT BEGINS"));
						break;
					case 24:
						sendTitle(player, format("&e&lTHE FIG&6&lH&c&lT &e&lBEGINS"));
						break;
					case 26:
						sendTitle(player, format("&e&lTHE FIGH&6&lT &c&lB&e&lEGINS"));
						break;
					case 28:
						sendTitle(player, format("&e&lTHE FIGHT &6&lB&c&lE&e&lGINS"));
						break;
					case 30:
						sendTitle(player, format("&e&lTHE FIGHT B&6&lE&c&lG&e&lINS"));
						break;
					case 32:
						sendTitle(player, format("&e&lTHE FIGHT BE&6&lG&c&lI&e&lNS"));
						break;
					case 34:
						sendTitle(player, format("&e&lTHE FIGHT BEG&6&lI&c&lN&e&lS"));
						break;
					case 36:
						sendTitle(player, format("&e&lTHE FIGHT BEGI&6&lN&c&lS"));
						break;
					case 38:
						sendTitle(player, format("&e&lTHE FIGHT BEGIN&6&lS"));
						break;
					case 40:
						sendTitle(player, format("&e&lTHE FIGHT BEGINS"));
						break;
					case 43:
						sendTitle(player, format("&c&lTHE FIGHT BEGINS"));
						break;
					case 46:
						sendTitle(player, format("&6&lTHE FIGHT BEGINS"));
						break;
					case 49:
						sendTitle(player, format("&c&lTHE FIGHT BEGINS"));
						break;
					case 52:
						sendTitle(player, format("&6&lTHE FIGHT BEGINS"));
						break;
					case 55:
						sendTitle(player, format("&c&lTHE FIGHT BEGINS"));
						break;
					case 58:
						player.sendTitle(ChatColor.DARK_RED + "☠", format("&e&lTHE FIGHT BEGINS"), 0, 0, 10);
						this.cancel();
						break;

				}
			}
		}.runTaskTimer(ClassesFight.getPlugin(), 0, 1);
	}

	public Player getRandomInGamePlayer() {
		return CollectionUtils.randomElement(this.getPlayers());
	}

	private void sendTitle(Player player, String subtitle) {
		player.sendTitle(ChatColor.DARK_RED + "☠", subtitle, 0, 10, 0);
	}

	public String getCurrentMapName() {
		return GameMap.getCurrentMap().getMapName();
	}

	public Location getCurrentMapSpawnLocation() {
		return GameMap.getCurrentMap().getLocation();
	}

	public void forEachBut(Consumer<? super Player> who, Player but) {
		playerStorage.forEach(dummy -> {
			if (dummy != but)
				who.accept(dummy);
		});
	}

	public boolean isState(GameState what) {
		return this.gameState == what;
	}

	private void setState(GameState state) {
		this.setGameState(state);
	}

	public void forEachInGamePlayer(Consumer<Player> action) {
		this.playerStorage.forEach(action);
	}

	public List<Player> getNearbyPlayers(Player player, double d, double dd, double ddd, boolean includeSelf) {
		final List<Player> hash = getNearbyPlayers(player.getLocation(), d, dd, ddd);
		return hash.stream().filter(g -> !includeSelf && g != player).collect(Collectors.toList());
	}

	public List<Player> getNearbyPlayers(Player player, double d, double dd, double ddd) {
		return getNearbyPlayers(player, d, dd, ddd, false);
	}

	public List<Player> getNearbyPlayers(Location loc, double d, double dd, double ddd) {
		return getNearbyPlayers(loc, d, dd, ddd, null);
	}

	public List<Player> getNearbyPlayers(Location loc, double d, double dd, double ddd, Player dontInclude) {
		final List<Player> hash = new ArrayList<>();
		loc.getWorld().getNearbyEntities(loc, d, dd, ddd).iterator().forEachRemaining(entity -> {
			if (entity instanceof Player) {
				if (dontInclude != null & dontInclude == entity) {
					return;
				}
				hash.add((Player)entity);
			}
		});
		return hash;
	}

	public boolean canAbilitiesBeUsed() {
		return this.isGameInProgress() && this.arePlayersRevealed();
	}

	public List<Player> getPlayersExcept(Player viewer) {
		final List<Player> players = new ArrayList<>(getPlayers());
		players.remove(viewer);
		return players;
	}

	public GameState getState() {
		return this.gameState;
	}
}
