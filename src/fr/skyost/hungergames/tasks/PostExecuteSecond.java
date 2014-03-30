package fr.skyost.hungergames.tasks;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGames.Step;
import fr.skyost.hungergames.HungerGamesAPI;

public class PostExecuteSecond extends BukkitRunnable {
	
	@Override
	public void run() {
		HungerGames.currentStep = Step.GAME;
		final Random random = new Random();
		HungerGamesAPI.broadcastMessage(HungerGames.messages.Messages_5);
		HungerGames.tasks.set(1, -1);
		HungerGames.tasks.set(2, Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.instance, new RandomItems(), random.nextInt(HungerGames.config.Game_RandomItems_Delay) * 20));
		HungerGames.tasks.set(3, Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.instance, new RandomMessages(), random.nextInt(HungerGames.messages.RandomMessages_Delay) * 20));
	}
	
}
