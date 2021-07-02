package ru.hapyl.classesfight;

import kz.hapyl.spigotutils.SpigotUtils;
import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.CommandProcessor;
import kz.hapyl.spigotutils.module.command.SimpleAdminCommand;
import kz.hapyl.spigotutils.module.command.SimplePlayerAdminCommand;
import kz.hapyl.spigotutils.module.inventory.SignGUI;
import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import ru.hapyl.classesfight.annotations.Singleton;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassUltimate;
import ru.hapyl.classesfight.classes.iclass.Librarian;
import ru.hapyl.classesfight.commands.*;
import ru.hapyl.classesfight.commands.simple.*;
import ru.hapyl.classesfight.consumable.ConsumableManager;
import ru.hapyl.classesfight.cosmetics.EffectListener;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.DatabaseListener;
import ru.hapyl.classesfight.database.entry.ClassEntry;
import ru.hapyl.classesfight.feature.*;
import ru.hapyl.classesfight.feature.japangod.JapanGodListener;
import ru.hapyl.classesfight.feature.npc.NpcManager;
import ru.hapyl.classesfight.minigame.MinigameManager;
import ru.hapyl.classesfight.parkour.ParkourCourse;
import ru.hapyl.classesfight.parkour.ParkourListener;
import ru.hapyl.classesfight.parkour.ParkourManager;
import ru.hapyl.classesfight.quest.DailyLoginQuestReward;
import ru.hapyl.classesfight.quest.broadcast.Broadcaster;
import ru.hapyl.classesfight.quest.relic.RelicHunt;
import ru.hapyl.classesfight.quest.relic.RelicRunnerListener;
import ru.hapyl.classesfight.reloaded.clazz.ClassPreview;
import ru.hapyl.classesfight.stats.StatsContainer;
import ru.hapyl.classesfight.stats.StatsHologramManager;
import ru.hapyl.classesfight.tablist.Tablist;
import ru.hapyl.classesfight.utils.CFItemBuilder;
import ru.hapyl.classesfight.utils.CFItemBuilderListener;
import ru.hapyl.classesfight.utils.GameUtils;
import ru.hapyl.classesfight.utils.candle.CandleController;

public class ClassesFight {

	private static final boolean stableBuild = false;
	private static final String pluginVersion = "0.2.6";

	@Singleton private static JavaPlugin javaPlugin;
	@Singleton private GameManager gameManager;
	@Singleton private StatsContainer container;
	@Singleton private Tablist tablist;
	@Singleton private ParkourManager parkourManager;
	@Singleton private GameUI gameUI;
	@Singleton private RelicHunt relicHunt;
	@Singleton private AutoSave autoSave;
	@Singleton private MapFeatures mapFeatures;
	@Singleton private DailyLoginQuestReward dailyRewards;
	@Singleton private CandleController candles;
	@Singleton private GameItem gameItems;
	@Singleton private Broadcaster broadcaster;

	public ClassesFight(JavaPlugin plugin) {
		javaPlugin = plugin;
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public static boolean isStableBuild() {
		return stableBuild;
	}

	public static String getPluginVersion() {
		return pluginVersion;
	}

	public StatsContainer getStatsContainer() {
		return container;
	}

	public DailyLoginQuestReward getDailyRewards() {
		return dailyRewards;
	}

	public void onEnable() {

		new SpigotUtils(javaPlugin);

		/**@deprecated new ChestInventory(javaPlugin); */

		new CFItemBuilder(javaPlugin);

		/**
		 * In order to reduce static usage I'm using assignment at onEnable
		 */

		// Game Manager
		this.gameManager = new GameManager();

		// Stats Stuff
		this.container = new StatsContainer();
		this.container.loadFile();

		this.gameItems = new GameItem(javaPlugin); // game items
		this.tablist = new Tablist(javaPlugin); // Fancy Tablist
		this.gameUI = new GameUI(javaPlugin); // UI (scoreboard)
		this.relicHunt = new RelicHunt(javaPlugin); // Relics
		this.dailyRewards = new DailyLoginQuestReward(javaPlugin); // Daily rewards
		this.parkourManager = new ParkourManager(); // Parkour Manager

		this.broadcaster = new Broadcaster(javaPlugin); // broadcaster

		this.mapFeatures = new MapFeatures(javaPlugin);
		this.candles = new CandleController();

		this.autoSave = new AutoSave(javaPlugin, minutesToTicks(10));

		new MinigameManager(javaPlugin); // Minigame manager
		new ConsumableManager(javaPlugin); // Consumable manager

		handleCommands();
		handleEvents();

		GameMap.loadCurrentMap();

		final BukkitScheduler scheduler = Bukkit.getScheduler();

		scheduler.runTaskLater(javaPlugin, () -> NpcManager.getManager().createGlobalStuff(), 20L);
		scheduler.scheduleSyncRepeatingTask(javaPlugin, new RelicRunnerListener(), 0L, 10L);
		scheduler.scheduleSyncRepeatingTask(javaPlugin, parkourManager, 0L, 1L);
		scheduler.scheduleSyncRepeatingTask(javaPlugin, mapFeatures, 0L, 1L);
		scheduler.scheduleSyncRepeatingTask(javaPlugin, broadcaster, 12000L, 12000L);

		for (Player player : Bukkit.getOnlinePlayers()) {
			/** @important */
			for (ParkourCourse value : ParkourCourse.values()) {
				value.showHolograms(player);
			}
			Database.createInstance(player);
			this.gameUI.createScoreboard(player);
			//Border.reset(player);
			this.gameItems.giveAll(player);
			scheduler.runTaskLater(javaPlugin, () -> NpcManager.getManager().spawnNpc(player), 22);
		}

		addEvent(this.candles);

		javaPlugin.getConfig().options().copyDefaults(true);
		javaPlugin.saveConfig();

		//this.gameUI.updateScoreboards();
		ClassEntry.handleClassRejoin();
		GameUtils.setCollision(false);

		/**
		 * Create Stats Holograms
		 */

		final StatsHologramManager stats = StatsHologramManager.current();
		stats.createHolograms();
		stats.updateViewers();

		// simple commands
		final CommandProcessor processor = new CommandProcessor();
		processor.registerCommand(new FastTeleport(">"));
		processor.registerCommand(new ParkourCommand("parkour"));
		processor.registerCommand(new CosmeticCommand("cosmetic"));
		processor.registerCommand(new MinigameCommand("minigame"));
		processor.registerCommand(new TwerkCommand("twerk"));
		processor.registerCommand(new TestInventoryCommand("testinventory"));
		processor.registerCommand(new RelicCommand("relic"));
		processor.registerCommand(new AbilityCommand("ability"));
		processor.registerCommand(new ClassStatsCommand("classstats"));
		processor.registerCommand(new RedeemCommand("redeem"));
		processor.registerCommand(new RecordCommand("record"));
		processor.registerCommand(new TakerAddBonesCommand("takeraddbone"));
		processor.registerCommand(new TestStringFormatCommand("teststringformatter"));
		processor.registerCommand(new BlockLocCommand("blockloc"));
		processor.registerCommand(new CandleCommand("candle"));
		processor.registerCommand(new PatchNotesCommand("patchnotes"));
		processor.registerCommand(new ParticleCommand("par"));

		processor.registerCommand(new SimplePlayerAdminCommand("testresetdoor") {
			@Override
			protected void execute(Player player, String[] strings) {
				player.sendMessage("done");
				mapFeatures.resetDoor();
			}
		});

		processor.registerCommand(new SimpleAdminCommand("testleaders") {
			@Override
			protected void execute(CommandSender commandSender, String[] strings) {
				Chat.sendMessage((Player)commandSender, "&aThere you go!");
				Chat.sendMessage((Player)commandSender, StatsContainer.current().getTopThreeParkourLeaders(ParkourCourse.LOBBY.getParkour()));
			}
		});

		processor.registerCommand(new SimpleAdminCommand("resetdaily") {
			@Override
			protected void execute(CommandSender commandSender, String[] strings) {
				final Player player = (Player)commandSender;
				Chat.sendMessage(player, "&aReset your daily counter.");
				getDailyRewards().setLastDailyTime(player, System.currentTimeMillis() - 86400000L);
			}
		});

		processor.registerCommand(new SimpleAdminCommand("testpi") {

			@Override
			public String getUsage() {
				return "/testpi (radius) (step)";
			}

			@Override
			protected void execute(CommandSender commandSender, String[] args) {

				if (!(commandSender instanceof Player player)) {
					return;
				}

				if (args.length < 2) {
					sendInvalidUsageMessage(player);
					return;
				}

				final Location location = player.getLocation();
				final double radius = Validate.getDouble(args[0]);
				final double value = args[1].startsWith("/") ? Math.PI / Validate.getDouble(args[1].replace("/", "")) : Validate.getDouble(args[1]);

				Chat.sendMessage(player, "&aDisplaying circle in %s radius with %s step.", radius, value);

				for (double d = 0; d < Math.PI * 2; d += value) {
					final double x = radius * Math.sin(d);
					final double z = radius * Math.cos(d);
					location.add(x, 0, z);
					PlayerLib.spawnParticle(location, Particle.GLOW, 1, 0, 0, 0, 0);
					location.subtract(x, 0, z);
				}

			}
		});

		processor.registerCommand(new SimpleAdminCommand("testsigngui") {
			@Override
			protected void execute(CommandSender commandSender, String[] strings) {
				if (!(commandSender instanceof Player player)) {
					return;
				}
				new SignGUI(player, "Enter your name!") {
					@Override
					public void onResponse(Player player, String[] strings) {
						player.sendMessage(ChatColor.GREEN + "Your name is " + (strings.length > 0 ? strings[0] : "Not Specified"));
					}
				}.openMenu();
			}
		});

		processor.registerCommand(new SimpleAdminCommand("librarianaddgrimoire") {
			@Override
			protected void execute(CommandSender commandSender, String[] strings) {
				if (!(commandSender instanceof final Player player)) {
					return;
				}
				if (!GameUtils.isValidPlayer(player, ClassManager.LIBRARIAN)) {
					Chat.sendMessage(player, "&cCannot use now.");
					return;
				}
				Chat.sendMessage(player, "&aLevelling up your Grimoire.");
				((Librarian)ClassManager.LIBRARIAN.getTheClass()).levelUpGrimmore(player);
			}
		});

		processor.registerCommand(new SimpleAdminCommand("classpreview") {
			@Override
			protected void execute(CommandSender commandSender, String[] strings) {
				if (!(commandSender instanceof final Player player)) {
					return;
				}

				final ClassPreview old = ClassPreview.getPreview(player);
				if (old != null) {
					Chat.sendMessage(player, "&aStopped preview.");
					old.cleanUp();
					return;
				}

				if (strings.length != 1) {
					return;
				}

				final ClassManager clazz = Validate.getEnumValue(ClassManager.class, strings[0]);

				if (clazz == null) {
					Chat.sendMessage(player, "&cInvalid class!");
					return;
				}

				Chat.sendMessage(player, "&aStarted %s preview!", clazz.getDisplayName());
				new ClassPreview(player, clazz);

			}
		});

	}

	public GameItem getGameItems() {
		return gameItems;
	}

	public CandleController getCandles() {
		return candles;
	}

	private int minutesToTicks(int minutes) {
		return minutes * 60 * 20;
	}

	public Tablist getTablist() {
		return tablist;
	}

	public MapFeatures getMapFeatures() {
		return mapFeatures;
	}

	public ParkourManager getParkourManager() {
		return parkourManager;
	}

	public GameUI getGameUI() {
		return gameUI;
	}

	public RelicHunt getRelicHunt() {
		return relicHunt;
	}

	public void onDisable() {

		container.writeToFile();
		javaPlugin.saveConfig();

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			for (ParkourCourse value : ParkourCourse.values()) {
				value.hideHolograms(onlinePlayer);
			}
			Database.getDatabase(onlinePlayer).saveToFile();
			if (getGameManager().isGameInProgress()) {
				GameUtils.resetPlayer(onlinePlayer);
			}
		}

		// remove parkour blocks
		for (ParkourCourse value : ParkourCourse.values()) {
			value.getParkour().destroyBlocks(Bukkit.getWorlds().get(0));
		}

		Bukkit.getBossBars().forEachRemaining(BossBar::removeAll);
		this.gameUI.resetAll();

		GarbageCollector.onStop();
		StatsHologramManager.current().removeHolograms();

		// this.gameManager.stopGame();
		// CFItemBuilder.clear();

	}

	private void handleEvents() {
		addEvent
				(
						// cf item builder causes items not to work, might be because of static
						new GlobalListener(), this.mapFeatures, new CustomChat(), new Spectator(),
						new ClassUltimate(), new PlayerManager(), new JapanGodListener(),
						new DatabaseListener(), new EnderPearlController(), new ParkourListener(),
						new StatsHologramManager(), new EffectListener(), new RelicRunnerListener(), new CFItemBuilderListener()
				);
	}

	private void handleCommands() {
		new ShortcutsCommand();

		addCommand("gm", new GamemodeCommand());
		addCommand("damage", new DamageCommand());
		addCommand("vector", new VectorCommand());
		addCommand("ultimate", new GameUltimateCommand());
		addCommand("rubies", new RubyCommand());
		addCommand("build", new BuildCommand());
		addCommand("console", new ConsoleCommand());
		addCommand("database", new DatabaseCommand());
		addCommand("class", new ClassCommand(), true);
		addCommand("gameeffect", new GameEffectCommand(), true);
		addCommand("stat", new StatCommand(), true);
		addCommand("coins", new CoinsCommand(), true);
		addCommand("exp", new ExpCommand(), true);
		addCommand("play", new PlayEffectCommand(), true);
		addCommand("testwinfx", new WinCommand());
		addCommand("feedback", new FeedbackCommand());
		addCommand("launch", new LaunchCommand());
		addCommand("sound", new SoundCommand(), true);
		addCommand(new MappingCommands(), "start", "stop");

		// do not use yet
		// CommandBuilder.addCommand("hello", sender -> sender.sendMessage("hello"));

	}

	private void addCommand(String command, CommandExecutor executor, boolean includeCompleter) {
		javaPlugin.getCommand(command).setExecutor(executor);
		if (includeCompleter)
			javaPlugin.getCommand(command).setTabCompleter((TabCompleter)executor);
	}

	private void addCommand(String command, CommandExecutor executor) {
		addCommand(command, executor, false);
	}

	private void addCommand(CommandExecutor executor, String... commands) {
		for (String command : commands) {
			addCommand(command, executor);
		}
	}

	public void addEvent(Listener... listeners) {
		for (Listener listener : listeners) {
			addEvent(listener);
		}
	}

	public AutoSave getAutoSave() {
		return autoSave;
	}

	public void addEvent(Listener list) {
		javaPlugin.getServer().getPluginManager().registerEvents(list, javaPlugin);
	}

	public static JavaPlugin getPlugin() {
		return javaPlugin;
	}

	public static ClassesFight getSingleton() {
		return ClassesFightPlugin.getSingleton();
	}

	public static void debug(String str) {
		System.out.println("[DEBUG] " + str);
	}
}

