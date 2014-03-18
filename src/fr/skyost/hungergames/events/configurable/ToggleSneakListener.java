package fr.skyost.hungergames.events.configurable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import fr.skyost.hungergames.HungerGames;

public class ToggleSneakListener implements Listener {
	
	@EventHandler
	private final void onPlayerToggleSneak(final PlayerToggleSneakEvent event) {
		if(HungerGames.players.get(event.getPlayer()) != null) {
			event.setCancelled(true);
		}
	}
	
}
