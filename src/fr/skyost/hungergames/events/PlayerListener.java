package fr.skyost.hungergames.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGamesAPI;
import fr.skyost.hungergames.HungerGamesProfile;

public class PlayerListener implements Listener {
	
	@EventHandler
	private final void onPlayerQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		if(HungerGames.players.get(player) != null) {
			HungerGamesAPI.removePlayer(player, false);
		}
	}
	
	@EventHandler
	private final void onPlayerRespawn(final PlayerRespawnEvent event) {
		final HungerGamesProfile profile = HungerGames.players.get(event.getPlayer());
		if(profile != null) {
			event.setRespawnLocation(profile.getGeneratedLocation());
		}
	}
	
}
