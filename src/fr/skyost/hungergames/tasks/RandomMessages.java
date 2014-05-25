package fr.skyost.hungergames.tasks;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGamesAPI;

public class RandomMessages extends BukkitRunnable {
	
	private final Random random = new Random();
	private final BukkitScheduler scheduler = Bukkit.getScheduler();
	
	@Override
	public void run() {
		HungerGamesAPI.broadcastMessage(HungerGames.messages.randomMessagesMessages.get(random.nextInt(HungerGames.messages.randomMessagesMessages.size())));
		scheduler.scheduleSyncDelayedTask(HungerGames.instance, this, random.nextInt(HungerGames.messages.randomMessagesDelay * 20));
	}
	
}
