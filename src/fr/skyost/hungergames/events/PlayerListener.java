package fr.skyost.hungergames.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import fr.skyost.hungergames.HungerGames;

public class PlayerListener implements Listener {
	
	@EventHandler
	private final void onPlayerToggleSneak(final PlayerToggleSneakEvent event) {
		if(HungerGames.config.Game_AutoSneak && HungerGames.players.get(event.getPlayer()) != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private final void onPlayerQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final World world = player.getWorld();
		if(world.equals(HungerGames.lobby)|| world.equals(HungerGames.currentMap)) {
			HungerGames.removePlayer(player, null, false);
		}
	}
	
}
