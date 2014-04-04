package fr.skyost.hungergames.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGamesAPI;
import fr.skyost.hungergames.HungerGamesProfile;
import fr.skyost.hungergames.utils.JsonItemStack;

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
	
	@EventHandler
	private final void onPlayerInteract(final PlayerInteractEvent event) {
		final ItemStack item = event.getItem();
		if(item != null && item.equals(HungerGames.kitSelector)) {
			final Player player = event.getPlayer();
			if(player.hasPermission("hungergames.kits.use")) {
				player.openInventory(HungerGames.kitsMenu);
			}
			else {
				player.sendMessage(HungerGames.messages.PermissionMessage);
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private final void onInventoryClick(final InventoryClickEvent event) {
		if(event.getInventory().getName().equals(HungerGames.kitsMenu.getName())) {
			final Player player = (Player)event.getWhoClicked();
			final PlayerInventory inventory = player.getInventory();
			inventory.clear();
			inventory.addItem(HungerGames.kitSelector);
			for(final String item : HungerGames.config.Kits_List.get(event.getCurrentItem().getItemMeta().getDisplayName())) {
				inventory.addItem(JsonItemStack.fromJson(item).toItemStack());
			}
			event.setCancelled(true);
			player.closeInventory();
		}
	}
	
}
