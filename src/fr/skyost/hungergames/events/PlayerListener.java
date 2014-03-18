package fr.skyost.hungergames.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGamesAPI;

public class PlayerListener implements Listener {
	
	@EventHandler
	private final void onPlayerQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final World world = player.getWorld();
		if(world.equals(HungerGames.lobby)|| world.equals(HungerGames.currentMap)) {
			HungerGamesAPI.removePlayer(player, false);
		}
	}
	
}
