package fr.skyost.hungergames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.util.ChatPaginator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.google.common.base.CharMatcher;

import fr.skyost.hungergames.commands.HungerGamesCommand;
import fr.skyost.hungergames.commands.SubCommandsExecutor.CommandInterface;
import fr.skyost.hungergames.commands.subcommands.hungergames.*;
import fr.skyost.hungergames.events.*;
import fr.skyost.hungergames.events.configurable.*;
import fr.skyost.hungergames.utils.*;
import fr.skyost.hungergames.utils.hooks.*;

/**
 * The class where fields and others variables are stocked.
 * 
 * @author Skyost.
 */

public class HungerGames extends JavaPlugin {
	
	public static HungerGames instance;
	public static SpectatorsManager spectatorsManager;
	public static final LogsManager logsManager = new LogsManager();
	public static MultiverseHook multiverseHook;
	public static EffectLibHook effectLibHook;
	
	public static PluginConfig config;
	public static PluginMessages messages;
	public static WinnersFile winners;
	
	public static final Integer[] tasks = new Integer[]{-1, -1, -1, -1, -1, -1, -1};
	public static final List<Chunk> generatedChunks = new ArrayList<Chunk>();
	public static final HashMap<Player, HungerGamesProfile> players = new HashMap<Player, HungerGamesProfile>();
	public static final SortedMap<Integer, String> winnersMap = new TreeMap<Integer, String>(Collections.reverseOrder());
	public static final HashMap<Integer, ItemStack> randomItems = new HashMap<Integer, ItemStack>();
	public static final HashMap<Integer, ItemStack> rewards = new HashMap<Integer, ItemStack>();
	public static final HashMap<String, ItemStack[]> kits = new HashMap<String, ItemStack[]>();
	
	public static World lobby;
	public static File mapsFolder;
	public static World currentMap;
	public static int totalPlayers = 0;
	public static Step currentStep = Step.LOBBY;
	public static Pages pages;
	public static Inventory kitsMenu;
	public static ItemStack kitSelector;
	
	public enum Step {
		LOBBY,
		FIRST_COUNTDOWN,
		SECOND_COUNTDOWN,
		GAME;
	}
	
	@Override
	public final void onEnable() {
		try {
			final File dataFolder = this.getDataFolder();
			instance = this;
			config = new PluginConfig(dataFolder);
			config.load();
			messages = new PluginMessages(dataFolder);
			messages.load();
			winners = new WinnersFile(dataFolder);
			winners.load();
			lobby = Bukkit.getWorld(config.lobbyWorld);
			if(lobby == null) {
				lobby = Bukkit.createWorld(new WorldCreator(config.lobbyWorld).type(WorldType.FLAT).generateStructures(false));
			}
			if(config.logConsole) {
				logsManager.setLogger(new PluginLogger(this));
			}
			if(config.logFileEnable) {
				logsManager.setLogsFolder(new File(config.logFileDirectory));
			}
			final PluginManager manager = Bukkit.getPluginManager();
			logsManager.log("Enabling plugin...");
			final int winnersSize = winners.winners.size();
			if(winnersSize != 0)  {
				for(int i = 0; i != winnersSize; i++) {
					winnersMap.put(i, winners.winners.get(i));
				}
				pages = new Pages(winnersMap, ChatPaginator.OPEN_CHAT_PAGE_HEIGHT, ChatColor.AQUA + "------\n" + HungerGames.messages.message17.replace("/line-separator/", "\n"), CharMatcher.is('\n').countIn(HungerGames.messages.message17));
			}
			if(config.enableUpdater) {
				new Skyupdater(this, 75831, this.getDataFolder(), true, true);
			}
			if(config.enableMetrics) {
				new MetricsLite(this).start();
			}
			spectatorsManager = new SpectatorsManager(this, config.spectatorsMode);
			mapsFolder = new File(config.mapsFolder);
			if(!mapsFolder.exists()) {
				mapsFolder.mkdir();
			}
			if(!checkConfig()) {
				manager.disablePlugin(this);
				return;
			}
			registerEvents(manager);
			final Plugin multiverse = manager.getPlugin("Multiverse-Core");
			if(multiverse != null) {
				multiverseHook = new MultiverseHook(multiverse);
				logsManager.log("Multiverse hooked with success !");
			}
			else if(Skyupdater.compareVersions("1.7.3", Utils.getMinecraftServerVersion())) {
				logsManager.log("If you are using a server software which has a version lower than 1.7.3, please install Multiverse.", Level.SEVERE);
				manager.disablePlugin(this);
				return;
			}
			if(config.gameDeathNameSky) {
				if(manager.getPlugin("EffectLib") != null) {
					effectLibHook = new EffectLibHook();
					logsManager.log("EffectLib hooked with success !");
				}
				else {
					logsManager.log("EffectLib was not found, could not write names in the sky.", Level.WARNING);
				}
			}
			setupRandomItems();
			setupRewards();
			setupKits();
			currentMap = HungerGamesAPI.generateMap();
			final Messenger messenger = Bukkit.getMessenger();
			messenger.registerOutgoingPluginChannel(this, "BungeeCord");
			messenger.registerIncomingPluginChannel(this, "BungeeCord", new BungeeMessageListener());
			registerCommands();
		}
		catch(final InvalidConfigurationException ex) {
			ex.printStackTrace();
			logsManager.log("Check the documentation for the configs here : http://url.skyost.eu/caF.", Level.SEVERE);
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			ErrorReport.createReport(ex).report();
			logsManager.log("Error while enabling the plugin... Check the stacktrace above.");
		}
	}
	
	@Override
	public final void onDisable() {
		logsManager.log("Disabling plugin...");
		try {
			if(players.size() != 0) {
				for(final Player player : players.keySet()) {
					HungerGamesAPI.revertPlayer(player, true);
					if(spectatorsManager.hasSpectator(player)) {
						spectatorsManager.removeSpectator(player);
					}
				}
			}
			if(winners != null) {
				final int winnersSize = winners.winners.size();
				final int winnersMapSize = winnersMap.size();
				if(winnersMapSize > winnersSize) {
					for(int i = winnersSize; i != winnersMapSize; i++) {
						winners.winners.add(i, winnersMap.get(i));
					}
					winners.save();
				}
				players.clear();
			}
			if(currentMap != null) {
				HungerGamesAPI.deleteMap(currentMap);
			}
			if(effectLibHook != null) {
				effectLibHook.stop();
			}
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			ErrorReport.createReport(ex).report();
			logsManager.log("Error while disabling the plugin... Check the stacktrace above.");
		}
	}
	
	private final void setupRandomItems() throws IOException {
		final File gameRandomItemsDirectory = new File(config.gameRandomItemsDirectory);
		if(!gameRandomItemsDirectory.exists()) {
			gameRandomItemsDirectory.mkdir();
			Utils.writeToFile(new File(gameRandomItemsDirectory, "gold-sword.json"), new JsonItemStack(10, Material.GOLD_SWORD.name(), "§6Gold sword", "§oCan be useful.", Enchantment.DAMAGE_ALL.getName(), Long.valueOf(Enchantment.DAMAGE_ALL.getMaxLevel()), null).toJson("chances"));
			Utils.writeToFile(new File(gameRandomItemsDirectory, "emerald.json"), new JsonItemStack(20, Material.EMERALD.name(), "§aEmerald", "§oI know you love it too.", null, null, null).toJson("chances"));
			Utils.writeToFile(new File(gameRandomItemsDirectory, "coal.json"), new JsonItemStack(50, Material.COAL.name(), null, Arrays.asList("For Christmas.", "- Mom"), null, Long.valueOf(5)).toJson("chances"));
		}
		for(final File randomItemFile : gameRandomItemsDirectory.listFiles()) {
			if(!randomItemFile.isFile() || !randomItemFile.getName().endsWith(".json")) {
				continue;
			}
			final JsonItemStack randomItem = JsonItemStack.fromJson(Utils.getFileContent(randomItemFile, null), "chances");
			randomItems.put(Integer.parseInt(randomItem.getOtherData().toString()), randomItem.toItemStack());
		}
	}
	
	private final void setupRewards() throws IOException {
		final File gameRewardsDirectory = new File(config.gameRewardsDirectory);
		if(!gameRewardsDirectory.exists()) {
			gameRewardsDirectory.mkdir();
			Utils.writeToFile(new File(gameRewardsDirectory, "gold-ingot.json"), new JsonItemStack(1, Material.GOLD_INGOT.name(), "§6Congracubations !", null, null, null, Long.valueOf(3)).toJson("position"));
		}
		for(final File reward : gameRewardsDirectory.listFiles()) {
			if(!reward.isFile() || !reward.getName().endsWith(".json")) {
				continue;
			}
			final JsonItemStack item = JsonItemStack.fromJson(Utils.getFileContent(reward, null), "position");
			rewards.put(Integer.parseInt(item.getOtherData().toString()), item.toItemStack());
		}
	}
	
	private final void setupKits() throws IOException {
		kitSelector = new ItemStack(config.kitsSelectorMaterial);
		ItemMeta meta = kitSelector.getItemMeta();
		meta.setDisplayName(config.kitsSelectorName);
		kitSelector.setItemMeta(meta);
		final File kitsDirectory = new File(config.kitsDirectory);
		if(!kitsDirectory.exists()) {
			kitsDirectory.mkdir();
			HungerGamesAPI.createKit("§7Iron", new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_HELMET), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_BOOTS));
		}
		else {
			final File[] kits = kitsDirectory.listFiles();
			for(final File kit : kits) {
				if(!kit.isFile() || !kit.getName().endsWith(".json")) {
					continue;
				}
				final JSONObject kitData = (JSONObject)JSONValue.parse(Utils.getFileContent(kit, null));
				final List<ItemStack> content = new ArrayList<ItemStack>();
				final JSONArray serializedContent = (JSONArray)kitData.get("content");
				for(int i = 0; i != serializedContent.size(); i++) {
					content.add(JsonItemStack.fromJson(serializedContent.get(i).toString(), "").toItemStack());
				}
				HungerGamesAPI.createKit(kitData.get("name").toString(), content.toArray(new ItemStack[content.size()]), false);
			}
		}
	}
	
	private final void registerEvents(final PluginManager manager) {
		for(final Listener listener : new Listener[]{new DamageListener(), new EntityListener(), new PlayerListener(), new WorldListener()}) {
			manager.registerEvents(listener, this);
		}
		if(config.spectatorsEnable) {
			if(!config.spectatorsPermissionsChat) {
				manager.registerEvents(new AsyncChatListener(), this);
			}
			if(!config.spectatorsPermissionsInteract) {
				manager.registerEvents(new InteractListener(), this);
			}
			if(!config.spectatorsPermissionsPickupItems) {
				manager.registerEvents(new PickupItemListener(), this);
			}
		}
		if(config.gameMotdChange) {
			manager.registerEvents(new ServerListPingListener(), this);
		}
		if(config.gameAutoSneak) {
			manager.registerEvents(new ToggleSneakListener(), this);
		}
		if(config.lobbyProtect) {
			manager.registerEvents(new LobbyListener(), this);
		}
		if(config.gameDedicatedServer) {
			manager.registerEvents(new AutoJoinListener(), this);
		}
		if(config.gameBloodPlayers || config.gameBloodMobs) {
			manager.registerEvents(new BloodListener(), this);
		}
	}
	
	private final boolean checkConfig() throws IOException {
		boolean configDeleted = false;
		if(config.VERSION < 3) {
			final File configFile = config.getFile();
			Utils.copy(configFile, new File(configFile.getPath() + "-OLD"));
			configFile.delete();
			logsManager.log("Your configuration file had a wrong version. It has been deleted (but backed up).");
			configDeleted = true;
		}
		if(messages.VERSION < 2) {
			final File messagesFile = messages.getFile();
			Utils.copy(messagesFile, new File(messagesFile.getPath() + "-OLD"));
			messagesFile.delete();
			logsManager.log("Your messages file had a wrong version. It has been deleted (but backed up).");
			configDeleted = true;
		}
		if(winners.VERSION < 2) {
			final File winnersFile = winners.getFile();
			Utils.copy(winnersFile, new File(winnersFile.getPath() + "-OLD"));
			winnersFile.delete();
			logsManager.log("Your winners file had a wrong version. It has been deleted (but backed up).");
			configDeleted = true;
		}
		if(configDeleted) {
			logsManager.log("Please restart the plugin.");
			return false;
		}
		if(config.gameMinPlayers < 2) {
			logsManager.log("MinPlayers cannot be inferior than two !", Level.WARNING);
			return false;
		}
		if(config.gameMaxPlayers < config.gameMinPlayers) {
			logsManager.log("MinPlayers cannot be inferior than MaxPlayers !", Level.WARNING);
			return false;
		}
		if(config.mapsBordersMeta < 0 || config.mapsBordersMeta > 15) {
			logsManager.log("Borders_Meta cannot be inferior than zero and cannot be superior than fifteen !", Level.WARNING);
			return false;
		}
		if(config.mapsBordersEnable && config.gameSpawnDistance > config.mapsBordersRadius) {
			logsManager.log("SpawnDistance cannot be superior than BordersRadius !", Level.WARNING);
			return false;
		}
		if(config.lobbySpawnX == 0 && config.lobbySpawnY == 0 && config.lobbySpawnZ == 0) {
			logsManager.log("The coords of the lobby's spawn are invalid.", Level.WARNING);
		}
		if(config.gameDedicatedServer && !config.spectatorsEnable) {
			logsManager.log("You must enable spectators to use the dedicated mode.", Level.WARNING);
		}
		return true;
	}
	
	private final void registerCommands() {
		final HungerGamesCommand executor = new HungerGamesCommand();
		for(final CommandInterface subCommand : new CommandInterface[]{new InfosSubCommand(), new JoinSubCommand(), new KitSubCommand(), new LeaveSubCommand(), new SetLobbySubCommand(), new WinnersSubCommand()}) {
			executor.registerSubCommand(subCommand);
		}
		final PluginCommand pluginCommand = this.getCommand("hg");
		pluginCommand.setUsage(ChatColor.RED + pluginCommand.getUsage());
		pluginCommand.setExecutor(executor);
	}
	
}
