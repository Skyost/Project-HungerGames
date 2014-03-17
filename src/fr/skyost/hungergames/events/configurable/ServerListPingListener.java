package fr.skyost.hungergames.events.configurable;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import fr.skyost.hungergames.HungerGames;

public class ServerListPingListener implements Listener {
	
	@EventHandler
	private final void onServerListPing(final ServerListPingEvent event) {
		event.setMotd(HungerGames.getCurrentMotd());
	}
	
}
