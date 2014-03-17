package fr.skyost.hungergames.events.configurable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.skyost.hungergames.HungerGames;

public class InteractListener implements Listener {
	
	@EventHandler
	private final void onPlayerInteract(final PlayerInteractEvent event) {
		if(HungerGames.isSpectator(event.getPlayer()) && !HungerGames.config.Spectators_Permissions_Interact) {
			event.setCancelled(true);
		}
	}
	
}
