package fr.skyost.hungergames.tasks;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGames.Step;
import fr.skyost.hungergames.HungerGamesAPI;

public class PostExecuteSecond extends BukkitRunnable {
	
	@Override
	public void run() {
		HungerGames.currentStep = Step.GAME;
		final BukkitScheduler scheduler = Bukkit.getScheduler();
		final Random random = new Random();
		HungerGamesAPI.broadcastMessage(HungerGames.messages.message5);
		HungerGames.tasks[1] = -1;
		HungerGames.tasks[2] = scheduler.scheduleSyncDelayedTask(HungerGames.instance, new RandomItems(), random.nextInt(HungerGames.config.gameRandomItemsDelay) * 20);
		HungerGames.tasks[3] = scheduler.scheduleSyncDelayedTask(HungerGames.instance, new RandomMessages(), random.nextInt(HungerGames.messages.randomMessagesDelay) * 20);
		if(HungerGames.config.gameCompassNearestPlayer) {
			HungerGames.tasks[6] = scheduler.scheduleSyncRepeatingTask(HungerGames.instance, new CompassOrienter(), 0L, 100L);
		}
	}
	
}
