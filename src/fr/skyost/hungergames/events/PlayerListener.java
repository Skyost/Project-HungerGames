package fr.skyost.hungergames.events;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGames.Step;
import fr.skyost.hungergames.HungerGamesAPI;
import fr.skyost.hungergames.HungerGamesProfile;
import fr.skyost.hungergames.utils.JsonItemStack;

public class PlayerListener implements Listener {
	
	@EventHandler
	private final void onPlayerQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		if(HungerGames.players.get(player) != null) {
			HungerGamesAPI.removePlayer(player, true);
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
	private final void onPlayerTeleport(final PlayerTeleportEvent event) {
		final Player player = event.getPlayer();
		final Location to = event.getTo();
		if(HungerGames.players.get(player) != null && ((HungerGames.currentStep == Step.GAME || HungerGames.currentStep == Step.SECOND_COUNTDOWN) && !to.getWorld().equals(HungerGames.currentMap) && !player.hasMetadata("Reverted"))) {
			HungerGamesAPI.removePlayer(player, true);
			player.teleport(to);
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
				player.sendMessage(HungerGames.messages.messagePermission);
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private final void onInventoryClick(final InventoryClickEvent event) {
		if(event.getInventory().getName().equals(HungerGames.kitsMenu.getName())) {
			final HumanEntity human = event.getWhoClicked();
			if(human != null && human instanceof Player) {
				final Player player = (Player)human;
				final ItemStack itemSelected = event.getCurrentItem();
				if(itemSelected != null) {
					final String kitName = itemSelected.getItemMeta().getDisplayName();
					if(HungerGames.config.kitsPermissions && !player.hasPermission("hungergames.kits." + ChatColor.stripColor(kitName).toLowerCase())) {
						player.sendMessage(HungerGames.messages.messagePermission);
						return;
					}
					final PlayerInventory inventory = player.getInventory();
					inventory.clear();
					inventory.setArmorContents(new ItemStack[]{null, null, null, null});
					inventory.addItem(HungerGames.kitSelector);
					final List<String> items = HungerGames.config.kitsList.get(kitName);
					if(items != null) {
						for(final String item : items) {
							inventory.addItem(JsonItemStack.fromJson(item).toItemStack());
						}
					}
					event.setCancelled(true);
					player.closeInventory();
				}
			}
		}
	}
	
}
