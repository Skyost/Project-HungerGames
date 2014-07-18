package fr.skyost.hungergames.events.configurable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import fr.skyost.hungergames.HungerGames;

public class LobbyListener implements Listener {
	
	@EventHandler
	private final void onPlayerInteract(final PlayerInteractEvent event) {
		if(event.getPlayer().getWorld().equals(HungerGames.lobby)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private final void onPlayerDropItem(final PlayerDropItemEvent event) {
		if(event.getPlayer().getWorld().equals(HungerGames.lobby)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private final void onPlayerPickupItem(final PlayerPickupItemEvent event) {
		if(event.getPlayer().getWorld().equals(HungerGames.lobby)) {
			event.setCancelled(true);
		}
	}
	
}
