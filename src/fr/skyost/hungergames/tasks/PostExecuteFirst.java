package fr.skyost.hungergames.tasks;

import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGamesProfile;
import fr.skyost.hungergames.HungerGames.Step;

public class PostExecuteFirst extends BukkitRunnable {
	
	@Override
	public void run() {
		HungerGames.currentStep = Step.SECOND_COUNTDOWN;
		final String message = HungerGames.messages.Messages_4.replaceAll("/n/", String.valueOf(HungerGames.config.Game_Countdown_Time));
		Player player;
		for(final Entry<Player, HungerGamesProfile> entry : HungerGames.players.entrySet()) {
			player = entry.getKey();
			player.teleport(entry.getValue().getGeneratedLocation());
			player.setGameMode(GameMode.SURVIVAL);
			player.setAllowFlight(false);
			player.setSneaking(HungerGames.config.Game_AutoSneak);
			player.setHealth(player.getMaxHealth());
			player.setFoodLevel(20);
			player.getInventory().removeItem(HungerGames.kitSelector);
			player.sendMessage(message);
		}
		HungerGames.tasks.set(0, new Countdown(HungerGames.config.Game_Countdown_Time, HungerGames.config.Game_Countdown_ExpBarLevel, new PostExecuteSecond()).runTaskTimer(HungerGames.instance, 0, 20L).getTaskId());
		HungerGames.tasks.set(1, -1);
	}
	
}
