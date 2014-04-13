package fr.skyost.hungergames.events.configurable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGamesAPI;
import fr.skyost.hungergames.HungerGames.Step;

public class AutoJoinListener implements Listener {
	
	@EventHandler
	private final void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		boolean spectator = false;
		if(HungerGames.currentStep == Step.SECOND_COUNTDOWN || HungerGames.currentStep == Step.GAME) {
			player.sendMessage(HungerGames.messages.Messages_12);
			spectator = true;
		}
		HungerGamesAPI.addPlayer(player, spectator);
	}
	
}
