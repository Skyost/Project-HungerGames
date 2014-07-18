package fr.skyost.hungergames.events.configurable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.skyost.hungergames.HungerGames;

public class AsyncChatListener implements Listener {
	
	@EventHandler
	private final void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
		if(HungerGames.spectatorsManager.hasSpectator(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	
}
