package fr.skyost.hungergames;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.ChatPaginator;

import com.google.common.base.CharMatcher;

import fr.skyost.hungergames.listeners.CommandsExecutor;
import fr.skyost.hungergames.listeners.EventsListener;
import fr.skyost.hungergames.tasks.Countdown;
import fr.skyost.hungergames.tasks.PostExecuteFirst;
import fr.skyost.hungergames.utils.MetricsLite;
import fr.skyost.hungergames.utils.Pages;
import fr.skyost.hungergames.utils.Skyupdater;
import fr.skyost.hungergames.utils.Utils;

public class HungerGames extends JavaPlugin {
	
	public static HungerGames instance;
	
	public static ConfigFile config;
	public static MessagesFile messages;
	public static WinnersFile winners;
	public static List<Integer> tasks = Arrays.asList(-1, -1, -1, -1);
	public static List<Chunk> generatedChunks = new ArrayList<Chunk>();
	public static SpectatorsManager spectatorsManager;
	public static List<Player> spectatorsList;
	
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
	
	public enum Mode {
		GHOST_FACTORY,
		INVISIBLE_POTION;
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
			if(config.Spectators_Enable) {
				if(config.Spectators_Mode == Mode.GHOST_FACTORY) {
					spectatorsManager = new SpectatorsManager(this);
				}
				else {
					spectatorsList = new ArrayList<Player>();
				}
			}
			lobby = Bukkit.getWorld(config.Lobby_World);
			if(lobby == null) {
				lobby = Bukkit.createWorld(new WorldCreator(config.Lobby_World));
			}
			mapsFolder = new File(config.Maps_Folder);
			if(!mapsFolder.exists()) {
				mapsFolder.mkdir();
			}
			if(config.Maps_Generate_Enable) {
				currentMap = Bukkit.createWorld(new WorldCreator(config.Maps_Generate_Name));
			}
			else {
				copyRandomMap();
			}
			Bukkit.getPluginManager().registerEvents(new EventsListener(), this);
			final PluginCommand command = this.getCommand("hunger-games");
			command.setUsage(ChatColor.RED + "/hg join, /hg leave, /hg infos or /hg winners <page>.");
			command.setExecutor(new CommandsExecutor());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public final void onDisable() {
		try {
			if(players.size() != 0) {
				for(final Player player : players.keySet()) {
					revertPlayer(player, true);
					if(isSpectator(player)) {
						if(config.Spectators_Mode == Mode.GHOST_FACTORY) {
							spectatorsManager.removeSpectator(player);
						}
						else {
							player.removePotionEffect(PotionEffectType.INVISIBILITY);
						}
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
			deleteCurrentMap();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static final void addPlayer(final Player player) {
		players.put(player, new HungerGamesProfile(player));
		final PlayerInventory inventory = player.getInventory();
		inventory.setArmorContents(new ItemStack[]{null, null, null, null});
		inventory.clear();
		Utils.updateInventory(player);
		player.setTotalExperience(0);
		player.setGameMode(GameMode.SURVIVAL);
		player.setFoodLevel(20);
		player.teleport(HungerGames.lobby.getSpawnLocation());
		totalPlayers++;
		broadcastMessage(messages.Messages_14.replaceAll("/n/", String.valueOf(totalPlayers)).replaceAll("/n-max/", String.valueOf(config.Game_MaxPlayers)).replaceAll("/player/", player.getName()));
		if(totalPlayers == config.Game_MinPlayers) {
			broadcastMessage(messages.Messages_3.replaceAll("/n/", String.valueOf(config.Lobby_Countdown_Time)));
			currentStep = Step.FIRST_COUNTDOWN;
			tasks.set(0, new Countdown(config.Lobby_Countdown_Time, config.Lobby_Countdown_ExpBarLevel, new PostExecuteFirst()).runTaskTimer(instance, 0, 20L).getTaskId());
		}
	}
	
	public static final void removePlayer(final Player player, final String message) {
		removePlayer(player, message, config.Spectators_Enable);
	}
	
	public static final void removePlayer(final Player player, final String message, final boolean setSpectator) {
		final boolean spectate = isSpectator(player);
		if(!spectate && setSpectator && totalPlayers > 2) {
			if(config.Spectators_Mode == Mode.GHOST_FACTORY) {
				spectatorsManager.addSpectator(player);
			}
			else {
				player.setAllowFlight(true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
				spectatorsList.add(player);
			}
		}
		else {
			if(spectate) {
				if(config.Spectators_Mode == Mode.GHOST_FACTORY) {
					spectatorsManager.removeSpectator(player);
				}
				else {
					player.removePotionEffect(PotionEffectType.INVISIBILITY);
				}
			}
			revertPlayer(player, false);
			players.remove(player);
		}
		player.sendMessage(message == null ? "" : message);
		totalPlayers--;
		if(currentStep == Step.GAME && totalPlayers == 1) {
			finishGame(messages.Messages_8);
		}
		else if(currentStep != Step.GAME && totalPlayers < config.Game_MinPlayers) {
			finishGame(messages.Messages_7);
		}
	}
	
	public static final boolean isSpectator(final Player player) {
		if(config.Spectators_Enable) {
			if(config.Spectators_Mode == Mode.GHOST_FACTORY) {
				return spectatorsManager.hasSpectator(player);
			}
			else {
				return spectatorsList.contains(player);
			}
		}
		return false;
	}
	
	public static final void finishGame(final String message) {
		final BukkitScheduler scheduler = Bukkit.getScheduler();
		for(final int task : tasks) {
			if(task != -1) {
				scheduler.cancelTask(task);
			}
		}
		for(final Player player : players.keySet()) {
			revertPlayer(player, true);
			if(isSpectator(player)) {
				if(config.Spectators_Mode == Mode.GHOST_FACTORY) {
					spectatorsManager.removeSpectator(player);
				}
				else {
					player.removePotionEffect(PotionEffectType.INVISIBILITY);
				}
			}
			else {
				player.sendMessage(message);
				winnersMap.put(winnersMap.size(), player.getName());
				pages = new Pages(winnersMap, ChatPaginator.OPEN_CHAT_PAGE_HEIGHT, ChatColor.AQUA + "------\n" + HungerGames.messages.Messages_17.replaceAll("/line-separator/", "\n"), CharMatcher.is('\n').countIn(HungerGames.messages.Messages_17));
			}
		}
		players.clear();
		deleteCurrentMap();
		if(config.Maps_Generate_Enable) {
			currentMap = Bukkit.createWorld(new WorldCreator(config.Maps_Generate_Name));
		}
		else {
			copyRandomMap();
		}
		totalPlayers = 0;
		currentStep = Step.LOBBY;
	}
	
	private static final void revertPlayer(final Player player, final boolean clearInventory) {
		revertPlayer(player, players.get(player), clearInventory);
	}
	
	
	private static final void revertPlayer(final Player player, final HungerGamesProfile profile, final boolean clearInventory) {
		final PlayerInventory inventory = player.getInventory();
		player.setTotalExperience(profile.getTotalExp());
		if(clearInventory) {
			inventory.setArmorContents(new ItemStack[]{null, null, null, null});
			inventory.clear();
			Utils.updateInventory(player);
		}
		inventory.setContents(profile.getInventoryContents());
		inventory.setArmorContents(profile.getInventoryArmorContents());
		Utils.updateInventory(player);
		player.setGameMode(profile.getGameMode());
		player.setAllowFlight(profile.getAllowFlight());
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.teleport(profile.getPreviousLocation());
	}
	
	public static final void broadcastMessage(final String message) {
		for(final Player player : players.keySet()) {
			player.sendMessage(message);
		}
	}
	
	public static final void deleteCurrentMap() {
		try {
			Bukkit.unloadWorld(currentMap, false);
			Utils.getMCClass("RegionFileCache").getMethod("a").invoke(null);
			Utils.delete(currentMap.getWorldFolder());
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static final void copyRandomMap() {
		final Logger logger = instance.getLogger();
		try {
			logger.log(Level.INFO, "Processing maps...");
			final File[] maps = mapsFolder.listFiles();
			if(maps.length == 0) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "The maps folder is empty ! Creating a new map...");
				currentMap = Bukkit.createWorld(new WorldCreator(config.Maps_Generate_Name));
				Utils.copy(currentMap.getWorldFolder(), new File(mapsFolder, config.Maps_Generate_Name));
			}
			else {
				final File currentWorld = maps[new Random().nextInt(maps.length)];
				final String currentWorldName = currentWorld.getName();
				Utils.copy(currentWorld, new File(currentWorldName));
				currentMap = Bukkit.createWorld(new WorldCreator(currentWorldName));
			}
			logger.log(Level.INFO, "Done ! The selected map is : '" + currentMap.getName() + "'.");
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.log(Level.SEVERE, "Error while processing maps... Check the stacktrace above.");
			Bukkit.getPluginManager().disablePlugin(instance);
		}
	}
	
	public static final String getCurrentMotd() {
		String motd = null;
		switch(HungerGames.currentStep) {
		case LOBBY:
			motd = HungerGames.messages.Motds_1.replaceAll("/n/", String.valueOf(HungerGames.config.Game_MinPlayers - HungerGames.totalPlayers));
			break;
		case FIRST_COUNTDOWN:
			motd = HungerGames.messages.Motds_2;
			break;
		case SECOND_COUNTDOWN:
			motd = HungerGames.messages.Motds_3;
			break;
		case GAME:
			motd = HungerGames.messages.Motds_4.replaceAll("/n/", String.valueOf(HungerGames.totalPlayers));
			break;
		}
		return motd;
	}
	
}
