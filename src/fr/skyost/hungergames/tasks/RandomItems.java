package fr.skyost.hungergames.tasks;

import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGamesAPI;
import fr.skyost.hungergames.utils.JsonItemStack;

public class RandomItems extends BukkitRunnable {
	
	private final BukkitScheduler scheduler = Bukkit.getScheduler();
	private static final Random random = new Random();
	private final int doubledRandomDistance = HungerGames.config.Game_RandomItems_Distance * 2;
	
	@Override
	public void run() {
		final Location location = HungerGames.currentMap.getSpawnLocation();
		location.add(random.nextInt(doubledRandomDistance) - HungerGames.config.Game_RandomItems_Distance + 1, 0, random.nextInt(doubledRandomDistance) - HungerGames.config.Game_RandomItems_Distance + 1);
		final int y = HungerGames.currentMap.getHighestBlockYAt(location);
		location.setY(y);
		final ItemStack item = pickRandomItem();
		if(HungerGames.config.Game_RandomItems_Chests) {
			final Block chestBlock = location.getBlock();
			chestBlock.setType(Material.CHEST);
			final Chest chest = (Chest)chestBlock.getState();
			chest.getInventory().addItem(item);
			final Block under = location.clone().subtract(0, 1, 0).getBlock();
			if(!under.getType().isSolid()) {
				under.setType(Material.GRASS);
			}
		}
		else {
			HungerGames.currentMap.dropItem(location, item);
		}
		HungerGamesAPI.broadcastMessage(HungerGames.messages.Messages_10.replaceAll("/x/", String.valueOf(location.getBlockX())).replaceAll("/y/", String.valueOf(y)).replaceAll("/z/", String.valueOf(location.getBlockZ())));
		if(HungerGames.config.Game_RandomItems_Thundering) {
			HungerGames.currentMap.strikeLightningEffect(location);
		}
		scheduler.scheduleSyncDelayedTask(HungerGames.instance, this, random.nextInt(HungerGames.config.Game_RandomItems_Delay * 20));
	}
	
	public static final ItemStack pickRandomItem() {
		int probability = 0;
		for(final String key : HungerGames.config.Game_Random_Items.keySet()) {
			probability += Integer.valueOf(key);
		}
		probability = random.nextInt(probability);
		int cumulativeProbability = 0;
		for(final Entry<String, String> entry : HungerGames.config.Game_Random_Items.entrySet()) {
			cumulativeProbability += Integer.valueOf(entry.getKey());
			if(probability <= cumulativeProbability) {
				return JsonItemStack.fromJson(entry.getValue()).toItemStack();
			}
		}
		return null;
	}
	
}
