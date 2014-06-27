package fr.skyost.hungergames;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.ChatPaginator;

import com.google.common.base.CharMatcher;

import fr.skyost.hungergames.HungerGames.Step;
import fr.skyost.hungergames.tasks.BorderCreatorTask;
import fr.skyost.hungergames.tasks.Countdown;
import fr.skyost.hungergames.tasks.PostExecuteFirst;
import fr.skyost.hungergames.utils.ErrorReport;
import fr.skyost.hungergames.utils.JsonItemStack;
import fr.skyost.hungergames.utils.Pages;
import fr.skyost.hungergames.utils.Utils;
import fr.skyost.hungergames.utils.borders.Border.Type;
import fr.skyost.hungergames.utils.borders.BorderParams;

/**
 * API class of Project HungerGames.
 * <br>Only contains statics methods, no need to instantiate.
 * 
 * @author Skyost
 */

public class HungerGamesAPI {
	
	/**
	 * Adds a player without adding him to the spectators manager.
	 * 
	 * @param player The player to add.
	 */
	
	public static final void addPlayer(final Player player) {
		addPlayer(player, false);
	}
	
	/**
	 * Adds a player.
	 * 
	 * @param player The player to add.
	 * @param setSpectator If you want to add the player to the spectators manager.
	 */
	
	public static final void addPlayer(final Player player, final boolean setSpectator) {
		HungerGames.logsManager.log(player.getName() + " joined the lobby.");
		HungerGames.players.put(player, new HungerGamesProfile(player));
		final PlayerInventory inventory = player.getInventory();
		inventory.setArmorContents(new ItemStack[]{null, null, null, null});
		inventory.clear();
		Utils.updateInventory(player);
		player.setLevel(0);
		player.setExp(0f);
		player.setGameMode(GameMode.SURVIVAL);
		player.setFoodLevel(20);
		if(setSpectator) {
			player.teleport(HungerGames.currentMap.getSpawnLocation());
			HungerGames.spectatorsManager.addSpectator(player);
		}
		else {
			inventory.addItem(HungerGames.kitSelector);
			final Location spawn;
			if(HungerGames.config.lobbySpawnX == 0 && HungerGames.config.lobbySpawnY == 0 && HungerGames.config.lobbySpawnZ == 0) {
				spawn = HungerGames.lobby.getSpawnLocation();
			}
			else {
				spawn = new Location(HungerGames.lobby, HungerGames.config.lobbySpawnX, HungerGames.config.lobbySpawnY, HungerGames.config.lobbySpawnZ);
			}
			player.teleport(spawn);
			HungerGames.totalPlayers++;
			broadcastMessage(HungerGames.messages.message14.replace("/n/", String.valueOf(HungerGames.totalPlayers)).replace("/n-max/", String.valueOf(HungerGames.config.gameMaxPlayers)).replace("/player/", player.getName()));
			if(HungerGames.totalPlayers == HungerGames.config.gameMinPlayers) {
				HungerGames.logsManager.log("Starting game...");
				broadcastMessage(HungerGames.messages.message3.replace("/n/", String.valueOf(HungerGames.config.lobbyCountdownTime)));
				HungerGames.currentStep = Step.FIRST_COUNTDOWN;
				HungerGames.tasks.set(0, new Countdown(HungerGames.config.lobbyCountdownTime, HungerGames.config.lobbyCountdownExpBarLevel, HungerGames.config.lobbyCountdownMobBar, new PostExecuteFirst()).runTaskTimer(HungerGames.instance, 0, 20L).getTaskId());
			}
		}
	}
	
	/**
	 * Removes a player and add it to the spectator manager if it has been set into the config file.
	 * 
	 * @param player The player to remove.
	 */
	
	public static final void removePlayer(final Player player) {
		removePlayer(player, HungerGames.config.spectatorsEnable);
	}
	
	/**
	 * Removes a player.
	 * 
	 * @param player The player to remove.
	 * @param setSpectator If you want to add the player to the spectators manager.
	 */

	public static final void removePlayer(final Player player, final boolean setSpectator) {
		if(HungerGames.spectatorsManager.hasSpectator(player)) {
			HungerGames.spectatorsManager.removeSpectator(player);
			revertPlayer(player, false);
			HungerGames.players.remove(player);
		}
		else {
			if(setSpectator && HungerGames.totalPlayers > 2) {
				HungerGames.spectatorsManager.addSpectator(player);
				player.teleport(HungerGames.players.get(player).getGeneratedLocation());
				player.setHealth(player.getMaxHealth());
				player.setFoodLevel(20);
			}
			else {
				revertPlayer(player, true);
				HungerGames.players.remove(player);
			}
			if(HungerGames.currentStep == Step.GAME && HungerGames.config.gameRewardsEnable) {
				giveReward(player, HungerGames.totalPlayers);
			}
			HungerGames.totalPlayers--;
		}
		if(HungerGames.currentStep != Step.LOBBY) {
			if(HungerGames.currentStep == Step.GAME && HungerGames.totalPlayers == 1) {
				finishGame(HungerGames.messages.message8, true);
			}
			else if((HungerGames.currentStep != Step.GAME && HungerGames.totalPlayers < HungerGames.config.gameMinPlayers) || Bukkit.getOnlinePlayers().length <= 1) {
				finishGame(HungerGames.messages.message7, false);
			}
		}
	}
	
	/**
	 * Finish the game.
	 * 
	 * @param message The message which will be sent to the winner.
	 * @param hasWinner If the game has a winner.
	 */
	
	public static final void finishGame(final String message, final boolean hasWinner) {
		HungerGames.logsManager.log("Finishing game...");
		final BukkitScheduler scheduler = Bukkit.getScheduler();
		for(final int task : HungerGames.tasks) {
			if(task != -1) {
				scheduler.cancelTask(task);
			}
		}
		for(final Player player : HungerGames.players.keySet()) {
			revertPlayer(player, true);
			if(HungerGames.spectatorsManager.hasSpectator(player)) {
				HungerGames.spectatorsManager.removeSpectator(player);
			}
			else {
				player.sendMessage(message);
				if(hasWinner) {
					if(HungerGames.config.gameRewardsEnable) {
						giveReward(player, HungerGames.totalPlayers);
					}
					HungerGames.winnersMap.put(HungerGames.winnersMap.size(), player.getName());
					HungerGames.pages = new Pages(HungerGames.winnersMap, ChatPaginator.OPEN_CHAT_PAGE_HEIGHT, ChatColor.AQUA + "------\n" + HungerGames.messages.message17.replace("/line-separator/", "\n"), CharMatcher.is('\n').countIn(HungerGames.messages.message17));
				}
			}
		}
		HungerGames.players.clear();
		deleteMap(HungerGames.currentMap);
		HungerGames.currentMap = generateMap();
		HungerGames.totalPlayers = 0;
		HungerGames.currentStep = Step.LOBBY;
		if(HungerGames.config.gameDedicatedServer) {
			for(final Player player : Bukkit.getOnlinePlayers()) {
				addPlayer(player, false);
			}
		}
	}
	
	/**
	 * Gives the corresponding reward to the player.
	 * 
	 * @param player The player.
	 * @param value The reward's value.
	 */
	
	public static final void giveReward(final Player player, final int value) {
		if(HungerGames.currentStep == Step.GAME || HungerGames.currentStep == Step.SECOND_COUNTDOWN) {
			final String jsonItemStack = HungerGames.config.gameRewards.get(String.valueOf(value));
			if(jsonItemStack != null) {
				final ItemStack item = JsonItemStack.fromJson(jsonItemStack).toItemStack();
				if(HungerGames.spectatorsManager.hasSpectator(player)) {
					HungerGames.players.get(player).addReward(item);
				}
				else {
					player.getInventory().addItem(item);
				}
			}
		}
	}
	
	/**
	 * Reverts a player back with his Hunger Games profile but does not remove it from the game.
	 * 
	 * @param player The player.
	 * @param clearInventory Clear the current player inventory.
	 */
	
	public static final void revertPlayer(final Player player, final boolean clearInventory) {
		revertPlayer(player, HungerGames.players.get(player), clearInventory);
	}
	
	/**
	 * Reverts a player back but does not remove it from the game.
	 * 
	 * @param player The player.
	 * @param profile The Hunger Games profile used to restore the player's settings.
	 * @param clearInventory Clear the current player inventory.
	 */
	
	public static final void revertPlayer(final Player player, final HungerGamesProfile profile, final boolean clearInventory) {
		player.setMetadata("Reverted", new FixedMetadataValue(HungerGames.instance, true));
		player.teleport(profile.getPreviousLocation());
		final PlayerInventory inventory = player.getInventory();
		if(clearInventory) {
			inventory.setArmorContents(new ItemStack[]{null, null, null, null});
			inventory.clear();
			Utils.updateInventory(player);
		}
		inventory.setContents(profile.getInventoryContents());
		inventory.setArmorContents(profile.getArmorContents());
		Utils.updateInventory(player);
		player.setLevel(profile.getExpLevel());
		player.setExp(profile.getExp());
		player.setGameMode(profile.getGameMode());
		player.setAllowFlight(profile.getAllowFlight());
		player.setSneaking(profile.isSneaking());
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.removeMetadata("Reverted", HungerGames.instance);
	}
	
	/**
	 * Broadcasts a message to the current players (including spectators).
	 * 
	 * @param message The message.
	 */
	
	public static final void broadcastMessage(final String message) {
		for(final Player player : HungerGames.players.keySet()) {
			player.sendMessage(message);
		}
	}
	
	/**
	 * Deletes a world. NMS is used but with reflection if Multiverse is not presents.
	 * 
	 * @param world The map to be deleted.
	 */
	
	public static final void deleteMap(final World world) {
		try {
			HungerGames.logsManager.log("Deleting the current map...");
			final List<Player> players = world.getPlayers();
			if(players.size() != 0) {
				for(final Player player : players) {
					player.teleport(HungerGames.lobby.getSpawnLocation());
				}
			}
			if(HungerGames.multiverseUtils == null) {
				Bukkit.unloadWorld(world, false);
				Utils.getMCClass("RegionFileCache").getMethod("a").invoke(null);
				Utils.delete(world.getWorldFolder());
			}
			else {
				HungerGames.multiverseUtils.deleteWorld(world.getName());
			}
			HungerGames.logsManager.log("Done !");
		}
		catch(ClassNotFoundException ex) {
			HungerGames.logsManager.log("You server version seems not compatible with reflection. Try downloading and installing Multiverse to fix this error.", Level.WARNING);
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			ErrorReport.createReport(ex).report();
			HungerGames.logsManager.log("Error while deleting the current map... Check the stacktrace above.");
		}
	}
	
	/**
	 * Generates a new map or chose it from the maps folder.
	 * 
	 * @return The world which is generated (or chosed).
	 */
	
	public static final World generateMap() {
		try {
			final World world;
			if(HungerGames.config.mapsGenerateEnable) {
				world = createWorld(HungerGames.config.mapsGenerateName);
			}
			else {
				HungerGames.logsManager.log("Processing maps...");
				final List<File> maps = new ArrayList<File>();
				for(final File map : HungerGames.mapsFolder.listFiles()) {
					if(map.isDirectory()) {
						maps.add(map);
					}
					else {
						HungerGames.logsManager.log(map.getName() + " is in the maps folder but is not a folder ! If it is an archive, you should unzip it.", Level.WARNING);
					}
				}
				if(maps.size() == 0) {
					HungerGames.logsManager.log("The maps folder is empty ! Creating a new map...", Level.WARNING);
					world = createWorld(HungerGames.config.mapsGenerateName);
					Utils.copy(world.getWorldFolder(), new File(HungerGames.mapsFolder, HungerGames.config.mapsGenerateName));
				}
				else {
					final File currentWorld = maps.get(new Random().nextInt(maps.size()));
					final String currentWorldName = currentWorld.getName();
					Utils.copy(currentWorld, new File(currentWorldName));
					world = createWorld(currentWorldName);
				}
				HungerGames.logsManager.log("Done ! The selected map is : '" + world.getName() + "'.");
			}
			if(HungerGames.config.mapsBordersEnable) {
				HungerGamesAPI.addBorders(world);
			}
			String gameRule;
			for(final Entry<String, String> entry : HungerGames.config.mapsGameRules.entrySet()) {
				gameRule = entry.getKey();
				if(world.isGameRule(gameRule)) {
					world.setGameRuleValue(gameRule, entry.getValue());
				}
				else {
					HungerGames.logsManager.log("'" + gameRule + "' is not a valid game rule !", Level.WARNING);
				}
			}
			world.setTime(HungerGames.config.mapsDefaultTime);
			return world;
		}
		catch(final Exception ex) {
			ex.printStackTrace();
			ErrorReport.createReport(ex).report();
			HungerGames.logsManager.log("Error while processing maps... Check the stacktrace above.");
			Bukkit.getPluginManager().disablePlugin(HungerGames.instance);
		}
		return null;
	}
	
	/**
	 * Creates a new world with or without Multiverse.
	 * 
	 * @param name The world name.
	 * 
	 * @return The created world.
	 */
	
	private static final World createWorld(final String name) {
		final World world = Bukkit.createWorld(new WorldCreator(name));
		if(HungerGames.multiverseUtils == null) {
			return world;
		}
		final ChunkGenerator generator = world.getGenerator();
		HungerGames.multiverseUtils.createWorld(name, world.getEnvironment(), world.getSeed(), world.getWorldType(), world.canGenerateStructures(), generator == null ? null : generator.getClass().getName());
		return HungerGames.multiverseUtils.getWorld(name);
	}
	
	/**
	 * Adds configured borders to the specified world.
	 * 
	 * @param world The world.
	 */
	
	public static final void addBorders(final World world) {
		if(HungerGames.config.mapsBordersType != Type.INVISIBLE && Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
			HungerGames.logsManager.log("WorldEdit was not found !", Level.WARNING);
			return;
		}
		final Location spawn = world.getSpawnLocation();
		HungerGames.tasks.set(4, new BorderCreatorTask(new BorderParams(world.getName(), spawn.getBlockX(), spawn.getBlockZ(), HungerGames.config.mapsBordersRadius, HungerGames.config.mapsBordersType, Utils.getId(HungerGames.config.mapsBordersMaterial), HungerGames.config.mapsBordersMeta)).runTask(HungerGames.instance).getTaskId());
	}
	
	/**
	 * Gets the current Hunger Games motd.
	 * 
	 * @return The current Hunger Games motd.
	 */
	
	public static final String getCurrentMotd() {
		String motd = null;
		switch(HungerGames.currentStep) {
		case LOBBY:
			motd = HungerGames.messages.motd1.replace("/n/", String.valueOf(HungerGames.config.gameMinPlayers - HungerGames.totalPlayers));
			break;
		case FIRST_COUNTDOWN:
			motd = HungerGames.messages.motd2;
			break;
		case SECOND_COUNTDOWN:
			motd = HungerGames.messages.motd3;
			break;
		case GAME:
			motd = HungerGames.messages.motd4.replace("/n/", String.valueOf(HungerGames.totalPlayers));
			break;
		}
		return motd;
	}
	
}
