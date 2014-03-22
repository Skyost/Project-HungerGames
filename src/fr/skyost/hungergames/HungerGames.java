package fr.skyost.hungergames;

import java.io.File;
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
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.ChatPaginator;

import com.google.common.base.CharMatcher;

import fr.skyost.hungergames.commands.HungerGamesCommand;
import fr.skyost.hungergames.events.DamageListener;
import fr.skyost.hungergames.events.EntityListener;
import fr.skyost.hungergames.events.PlayerListener;
import fr.skyost.hungergames.events.WorldListener;
import fr.skyost.hungergames.events.configurable.AsyncChatListener;
import fr.skyost.hungergames.events.configurable.InteractListener;
import fr.skyost.hungergames.events.configurable.LobbyListener;
import fr.skyost.hungergames.events.configurable.PickupItemListener;
import fr.skyost.hungergames.events.configurable.ServerListPingListener;
import fr.skyost.hungergames.events.configurable.ToggleSneakListener;
import fr.skyost.hungergames.utils.MetricsLite;
import fr.skyost.hungergames.utils.Pages;
import fr.skyost.hungergames.utils.Skyupdater;

/**
 * The class where fields and others variables are stocked.
 * 
 * @author Skyost.
 */

public class HungerGames extends JavaPlugin {
	
	public static HungerGames instance;
	
	public static PluginLogger logger;
	public static ConfigFile config;
	public static MessagesFile messages;
	public static WinnersFile winners;
	public static List<Integer> tasks = Arrays.asList(-1, -1, -1, -1, -1, -1);
	public static List<Chunk> generatedChunks = new ArrayList<Chunk>();
	public static SpectatorsManager spectatorsManager;
	
	public static World lobby;
	public static File mapsFolder;
	public static World currentMap;
	public static final HashMap<Player, HungerGamesProfile> players = new HashMap<Player, HungerGamesProfile>();
	public static int totalPlayers = 0;
	public static Step currentStep = Step.LOBBY;
	public static final SortedMap<Integer, String> winnersMap = new TreeMap<Integer, String>(Collections.reverseOrder());
	public static Pages pages;
	
	public enum Step {
		LOBBY,
		FIRST_COUNTDOWN,
		SECOND_COUNTDOWN,
		GAME;
	}
	
	@Override
	public final void onEnable() {
		try {
			instance = this;
			config = new ConfigFile(this.getDataFolder());
			config.init();
			messages = new MessagesFile(this.getDataFolder());
			messages.init();
			winners = new WinnersFile(this.getDataFolder());
			winners.init();
			if(config.Log_Console) {
				logger = new PluginLogger(this);
				logger.log(Level.INFO, "Enabling plugin...");
			}
			final int winnersSize = winners.Winners.size();
			if(winnersSize != 0)  {
				for(int i = 0; i != winnersSize; i++) {
					winnersMap.put(i, winners.Winners.get(i));
				}
				pages = new Pages(winnersMap, ChatPaginator.OPEN_CHAT_PAGE_HEIGHT, ChatColor.AQUA + "------\n" + HungerGames.messages.Messages_17.replaceAll("/line-separator/", "\n"), CharMatcher.is('\n').countIn(HungerGames.messages.Messages_17));
			}
			if(config.EnableUpdater) {
				new Skyupdater(this, 75831, this.getDataFolder(), true, true);
			}
			if(config.EnableMetrics) {
				new MetricsLite(this).start();
			}
			spectatorsManager = new SpectatorsManager(this, config.Spectators_Mode);
			mapsFolder = new File(config.Maps_Folder);
			if(!mapsFolder.exists()) {
				mapsFolder.mkdir();
			}
			currentMap = HungerGamesAPI.generateMap();
			final PluginManager manager = Bukkit.getPluginManager();
			registerEvents(manager);
			if(!checkConfig()) {
				manager.disablePlugin(this);
			}
			final PluginCommand command = this.getCommand("hunger-games");
			command.setUsage(ChatColor.RED + "/hg join, /hg leave, /hg infos or /hg winners <page>.");
			command.setExecutor(new HungerGamesCommand());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.log(Level.SEVERE, "Error while enabling the plugin... Check the stacktrace above.");
		}
	}
	
	@Override
	public final void onDisable() {
		if(config.Log_Console) {
			logger.log(Level.INFO, "Disabling plugin...");
		}
		try {
			if(players.size() != 0) {
				for(final Player player : players.keySet()) {
					HungerGamesAPI.revertPlayer(player, true);
					if(spectatorsManager.hasSpectator(player)) {
						spectatorsManager.removeSpectator(player);
					}
				}
			}
			final int winnersSize = winners.Winners.size();
			final int winnersMapSize = winnersMap.size();
			if(winnersMapSize > winnersSize) {
				for(int i = winnersSize; i != winnersMapSize; i++) {
					winners.Winners.add(i, winnersMap.get(i));
				}
			}
			winners.save();
			players.clear();
			HungerGamesAPI.deleteMap(currentMap);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.log(Level.SEVERE, "Error while disabling the plugin... Check the stacktrace above.");
		}
	}
	
	private final void registerEvents(final PluginManager manager) {
		for(final Listener listener : new Listener[]{new DamageListener(), new EntityListener(), new PlayerListener(), new WorldListener()}) {
			manager.registerEvents(listener, this);
		}
		if(config.Game_Motd_Change) {
			manager.registerEvents(new ServerListPingListener(), this);
		}
		if(config.Spectators_Enable) {
			if(!config.Spectators_Permissions_Chat) {
				manager.registerEvents(new AsyncChatListener(), this);
			}
			if(!config.Spectators_Permissions_Interact) {
				manager.registerEvents(new InteractListener(), this);
			}
			if(!config.Spectators_Permissions_PickupItems) {
				manager.registerEvents(new PickupItemListener(), this);
			}
		}
		if(config.Game_AutoSneak) {
			manager.registerEvents(new ToggleSneakListener(), this);
		}
		if(config.Lobby_Protect) {
			manager.registerEvents(new LobbyListener(), this);
		}
	}
	
	private final boolean checkConfig() {
		if(config.Game_MinPlayers < 2) {
			logger.log(Level.SEVERE, "MinPlayers cannot be inferior than two !");
			return false;
		}
		if(config.Game_MaxPlayers < config.Game_MinPlayers) {
			logger.log(Level.SEVERE, "MinPlayers cannot be inferior than MaxPlayers !");
			return false;
		}
		if(config.Maps_Borders_Meta < 0) {
			logger.log(Level.SEVERE, "Borders_Meta cannot be inferior than zero !");
			return false;
		}
		if(config.Maps_Borders_Enable && config.Game_SpawnDistance > config.Maps_Borders_Radius) {
			logger.log(Level.SEVERE, "SpawnDistance cannot be superior than BordersRadius !");
			return false;
		}
		return true;
	}
	
}
