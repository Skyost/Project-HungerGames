package fr.skyost.hungergames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.util.ChatPaginator;

import com.google.common.base.CharMatcher;

import fr.skyost.hungergames.commands.HungerGamesCommand;
import fr.skyost.hungergames.events.DamageListener;
import fr.skyost.hungergames.events.EntityListener;
import fr.skyost.hungergames.events.PlayerListener;
import fr.skyost.hungergames.events.WorldListener;
import fr.skyost.hungergames.events.configurable.AsyncChatListener;
import fr.skyost.hungergames.events.configurable.AutoJoinListener;
import fr.skyost.hungergames.events.configurable.InteractListener;
import fr.skyost.hungergames.events.configurable.LobbyListener;
import fr.skyost.hungergames.events.configurable.PickupItemListener;
import fr.skyost.hungergames.events.configurable.ServerListPingListener;
import fr.skyost.hungergames.events.configurable.ToggleSneakListener;
import fr.skyost.hungergames.utils.ErrorSender;
import fr.skyost.hungergames.utils.JsonItemStack;
import fr.skyost.hungergames.utils.LogsManager;
import fr.skyost.hungergames.utils.MetricsLite;
import fr.skyost.hungergames.utils.MultiverseHook;
import fr.skyost.hungergames.utils.Pages;
import fr.skyost.hungergames.utils.Skyupdater;
import fr.skyost.hungergames.utils.Utils;

/**
 * The class where fields and others variables are stocked.
 * 
 * @author Skyost.
 */

public class HungerGames extends JavaPlugin {
	
	public static HungerGames instance;
	public static SpectatorsManager spectatorsManager;
	public static final LogsManager logsManager = new LogsManager();
	public static MultiverseHook multiverseUtils;
	
	public static ConfigFile config;
	public static MessagesFile messages;
	public static WinnersFile winners;
	
	public static final List<Integer> tasks = Arrays.asList(-1, -1, -1, -1, -1, -1);
	public static final List<Chunk> generatedChunks = new ArrayList<Chunk>();
	public static final HashMap<Player, HungerGamesProfile> players = new HashMap<Player, HungerGamesProfile>();
	public static final SortedMap<Integer, String> winnersMap = new TreeMap<Integer, String>(Collections.reverseOrder());
	
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
			config = new ConfigFile(dataFolder);
			config.load();
			messages = new MessagesFile(dataFolder);
			messages.load();
			winners = new WinnersFile(dataFolder);
			winners.load();
			lobby = Bukkit.getWorld(config.lobbyWorld);
			if(lobby == null) {
				lobby = Bukkit.createWorld(new WorldCreator(config.lobbyWorld));
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
				pages = new Pages(winnersMap, ChatPaginator.OPEN_CHAT_PAGE_HEIGHT, ChatColor.AQUA + "------\n" + HungerGames.messages.message17.replaceAll("/line-separator/", "\n"), CharMatcher.is('\n').countIn(HungerGames.messages.message17));
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
			registerEvents(manager);
			if(!checkConfig()) {
				manager.disablePlugin(this);
				return;
			}
			final Plugin multiverse = manager.getPlugin("Multiverse-Core");
			if(multiverse != null) {
				multiverseUtils = new MultiverseHook(multiverse);
				logsManager.log("Multiverse hooked with success !");
			}
			else if(Skyupdater.compareVersions(Utils.getMinecraftServerVersion(), "1.6.5")) {
				logsManager.log("If you are using a server software which has a version lower than 1.7.3, please install Multiverse.", Level.SEVERE);
				manager.disablePlugin(this);
				return;
			}
			kitSelector = new ItemStack(config.kitsSelectorMaterial);
			ItemMeta meta = kitSelector.getItemMeta();
			meta.setDisplayName(config.kitsSelectorName);
			kitSelector.setItemMeta(meta);
			kitsMenu = Bukkit.createInventory(null, Utils.round(config.kitsList.size(), 9), config.kitsSelectorName);
			ItemStack item;
			for(final Entry<String, List<String>> entry : config.kitsList.entrySet()) {
				final String itemName = entry.getKey();
				item = new ItemStack(JsonItemStack.fromJson(entry.getValue().get(0)).toItemStack().getType());
				meta = item.getItemMeta();
				meta.setDisplayName(itemName);
				item.setItemMeta(meta);
				kitsMenu.addItem(item);
				if(config.kitsPermissions) {
					manager.addPermission(new Permission("hungergames.kits." + ChatColor.stripColor(itemName).toLowerCase()));
				}
			}
			currentMap = HungerGamesAPI.generateMap();
			final Messenger messenger = Bukkit.getMessenger();
			messenger.registerOutgoingPluginChannel(this, "BungeeCord");
			messenger.registerIncomingPluginChannel(this, "BungeeCord", new BungeeMessageListener());
			final PluginCommand command = this.getCommand("hg");
			command.setUsage(ChatColor.RED + command.getUsage());
			command.setExecutor(new HungerGamesCommand());
		}
		catch(InvalidConfigurationException ex) {
			ex.printStackTrace();
			logsManager.log("Check the documentation for the configurations files here : http://url.skyost.eu/caF.", Level.SEVERE);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ErrorSender.createReport(ex).report();
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
			final int winnersSize = winners.winners.size();
			final int winnersMapSize = winnersMap.size();
			if(winnersMapSize > winnersSize) {
				for(int i = winnersSize; i != winnersMapSize; i++) {
					winners.winners.add(i, winnersMap.get(i));
				}
				winners.save();
			}
			players.clear();
			if(currentMap != null) {
				HungerGamesAPI.deleteMap(currentMap);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			ErrorSender.createReport(ex).report();
			logsManager.log("Error while disabling the plugin... Check the stacktrace above.");
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
	}
	
	private final boolean checkConfig() throws IOException {
		boolean configDeleted = false;
		if(config.VERSION < 2) {
			final File configFile = config.getFile();
			Utils.copy(configFile, new File(configFile.getPath() + "-OLD"));
			configFile.delete();
			logsManager.log("Your winners file had a wrong version. It has been deleted (but backed up).");
			configDeleted = true;
		}
		if(messages.VERSION < 2) {
			final File messagesFile = messages.getFile();
			Utils.copy(messagesFile, new File(messagesFile.getPath() + "-OLD"));
			messagesFile.delete();
			logsManager.log("Your winners file had a wrong version. It has been deleted (but backed up).");
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
		return true;
	}
	
}
