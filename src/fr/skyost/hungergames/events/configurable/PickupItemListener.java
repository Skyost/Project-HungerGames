package fr.skyost.hungergames.events.configurable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import fr.skyost.hungergames.HungerGames;

public class PickupItemListener implements Listener {
	
	@EventHandler
	private final void onPlayerPickupItem(final PlayerPickupItemEvent event) {
		if(HungerGames.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
}
