package fr.skyost.hungergames;

import java.io.Serializable;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Represents a player profile.
 * 
 * @author Skyost.
 */

public class HungerGamesProfile implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final Location previousLocation;
	private final int totalExp;
	private final ItemStack[] items;
	private final ItemStack[] armor;
	private final GameMode gameMode;
	private final boolean allowFlight;
	private final boolean isSneaking;
	
	private final Location generatedLocation;

	/**
	 * Constructs a player profile.
	 * 
	 * @param player The player.
	 */
	
	public HungerGamesProfile(final Player player) {
		previousLocation = player.getLocation();
		totalExp = player.getTotalExperience();
		final PlayerInventory inventory = player.getInventory();
		items = inventory.getContents();
		armor = inventory.getArmorContents();
		gameMode = player.getGameMode();
		allowFlight = player.getAllowFlight();
		generatedLocation = HungerGames.currentMap.getSpawnLocation();
		isSneaking = player.isSneaking();
		final Random random = new Random();
		final int doubledDistance = HungerGames.config.Game_SpawnDistance * 2;
		final int x = random.nextInt(doubledDistance) - HungerGames.config.Game_SpawnDistance + 1;
		final int z = random.nextInt(doubledDistance) - HungerGames.config.Game_SpawnDistance + 1;
		generatedLocation.add(x, 100, z);
		final Chunk chunk = HungerGames.currentMap.getChunkAt(x, z);
		chunk.load(true);
		HungerGames.generatedChunks.add(chunk);
	}

	
	/**
	 * Get the previous location of the player.
	 * 
	 * @return The previous location of the player.
	 */
	
	public final Location getPreviousLocation() {
		return previousLocation;
	}
	
	/**
	 * Get the previous experience of the player.
	 * 
	 * @return The previous experience of the player.
	 */
	
	public final int getTotalExp() {
		return totalExp;
	}
	
	/**
	 * Get the inventory contents of the player.
	 * 
	 * @return The inventory contents of the player.
	 */
	
	public final ItemStack[] getInventoryContents() {
		return items;
	}
	
	/**
	 * Get the inventory armor contents of the player.
	 * 
	 * @return The inventory armor contents of the player.
	 */
	
	public final ItemStack[] getInventoryArmorContents() {
		return armor;
	}

	
	/**
	 * Get the previous game mode of the player.
	 * 
	 * @return The previous game mode of the player.
	 */
	
	public final GameMode getGameMode() {
		return gameMode;
	}
	
	/**
	 * If the player had the right to fly.
	 * 
	 * @return <b>true</b> If the player had the right to fly.
	 * <br><b>false</b> If the player does not had the right to fly.
	 */
	
	public final boolean getAllowFlight() {
		return allowFlight;
	}
	
	/**
	 * If the player is sneaking.
	 * 
	 * @return <b>true</b> If the player is sneaking.
	 * <br><b>false</b> If the player is not sneaking.
	 */
	
	public final boolean isSneaking() {
		return isSneaking;
	}

	
	/**
	 * Get the generated location of the player.
	 * 
	 * @return The generated location of the player.
	 */
	
	public final Location getGeneratedLocation() {
		return generatedLocation;
	}
	
}
