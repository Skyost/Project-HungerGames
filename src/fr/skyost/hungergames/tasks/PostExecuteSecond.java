package fr.skyost.hungergames.tasks;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGames.Step;

public class PostExecuteSecond extends BukkitRunnable {
	
	@Override
	public void run() {
		final Random random = new Random();
		HungerGames.currentStep = Step.GAME;
		HungerGames.broadcastMessage(HungerGames.messages.Messages_5);
		HungerGames.tasks.set(1, -1);
		HungerGames.tasks.set(2, Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.instance, new RandomItems(), random.nextInt(HungerGames.config.Game_Random_Delay) * 20));
		HungerGames.tasks.set(3, Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.instance, new RandomMessages(), random.nextInt(HungerGames.messages.RandomMessages_Delay) * 20));
	}
	
}
