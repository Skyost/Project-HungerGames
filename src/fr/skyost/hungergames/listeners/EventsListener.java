package fr.skyost.hungergames.listeners;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGames.Step;
import fr.skyost.hungergames.utils.Utils;

public class EventsListener implements Listener {
	
	@EventHandler
	private final void onEntityDamageEvent(final EntityDamageEvent event) {
		final Entity entity = event.getEntity();
		if(entity.getType() == EntityType.PLAYER) {
			final Player player = (Player)entity;
			final World world = entity.getWorld();
			if((world.equals(HungerGames.lobby) || HungerGames.isSpectator(player) || (world.equals(HungerGames.currentMap) && HungerGames.currentStep != Step.GAME))) {
				event.setCancelled(true);
			}
			else if(world.equals(HungerGames.currentMap) && HungerGames.currentStep == Step.GAME) {
				if(player.getGameMode() != GameMode.CREATIVE && player.getHealth() - event.getDamage() <= 0.0) {
					event.setCancelled(true);
					final Location location = player.getLocation();
					final LivingEntity killer = player.getKiller();
					if(killer != null && killer.getType() == EntityType.PLAYER) {
						final Player killerPlayer = (Player)killer;
						final int exp = Utils.getPlayerXP(player);
						killerPlayer.setTotalExperience(killerPlayer.getTotalExperience() + exp);
						killerPlayer.sendMessage(HungerGames.messages.Messages_15.replaceAll("/n/", String.valueOf(exp)).replaceAll("/player/", player.getName()));
					}
					final PlayerInventory inventory = player.getInventory();
					for(final ItemStack item : inventory.getContents()) {
						if(item != null && item.getType() != Material.AIR) {
							world.dropItem(location, item);
							inventory.remove(item);
						}
					}
					for(final ItemStack item : inventory.getArmorContents()) {
						if(item != null && item.getType() != Material.AIR) {
							world.dropItem(location, item);
						}
					}
					inventory.setArmorContents(new ItemStack[]{null, null, null, null});
					Utils.updateInventory(player);
					for(final Player playerKey : HungerGames.players.keySet()) {
						playerKey.sendMessage(HungerGames.messages.DeathMessages.get(new Random().nextInt(HungerGames.messages.DeathMessages.size())).replaceAll("/player/", player.getName()));
						world.playSound(playerKey.getLocation(), HungerGames.config.Game_DeathSound_Sound, Float.parseFloat(HungerGames.config.Game_DeathSound_Volume), Float.parseFloat(HungerGames.config.Game_DeathSound_Pitch));
					}
					HungerGames.removePlayer(player, HungerGames.config.Spectators_Enable ? HungerGames.messages.Messages_9 + (HungerGames.totalPlayers <= 2 ? "" : "\n" + HungerGames.messages.Messages_12) : HungerGames.messages.Messages_9);
				}
			}
		}
	}
	
	@EventHandler
	private final void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
		if(HungerGames.isSpectator(event.getPlayer()) && !HungerGames.config.Spectators_Permissions_Chat) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private final void onPlayerPickupItemEvent(final PlayerPickupItemEvent event) {
		if(HungerGames.isSpectator(event.getPlayer()) && !HungerGames.config.Spectators_Permissions_PickupItems) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	private final void onPlayerInteract(final PlayerInteractEvent event) {
		if(HungerGames.isSpectator(event.getPlayer()) && !HungerGames.config.Spectators_Permissions_Interact) {
			event.setCancelled(true);
		}
	}
	
	/**
	 * @author asofold.
	 */
	
	@EventHandler(priority = EventPriority.LOW)
	private final void onEntityTarget(final EntityTargetEvent event) {
		if(event.isCancelled()) {
			return;
		}	
		final Entity entity = event.getEntity();
		final Entity target = event.getTarget();
		if(target instanceof Player) {
			final Player player = (Player)target;
			if(HungerGames.isSpectator(player) && entity instanceof ExperienceOrb) {
				Utils.repellExpOrb(player, (ExperienceOrb)entity);
				event.setCancelled(true);
				event.setTarget(null);
				return;
			}
		}
	}
	
	@EventHandler
	private final void onFoodLevelChange(final FoodLevelChangeEvent event) {
		final HumanEntity entity = event.getEntity();
		if(entity.getType() == EntityType.PLAYER) {
			final World world = entity.getWorld();
			if((world.equals(HungerGames.lobby) || HungerGames.isSpectator((Player)entity) || (world.equals(HungerGames.currentMap) && HungerGames.currentStep != Step.GAME))) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	private final void onChunkUnloadEvent(final ChunkUnloadEvent event) {
		final Chunk chunk = event.getChunk();
		if(HungerGames.generatedChunks.contains(chunk)) {
			if(HungerGames.currentStep == Step.GAME) {
				HungerGames.generatedChunks.remove(chunk);
			}
			else {
				event.setCancelled(true);
			}
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
	
	@EventHandler
	private final void onServerListPingEvent(final ServerListPingEvent event) {
		if(HungerGames.config.Game_Motd_Change) {
			event.setMotd(HungerGames.getCurrentMotd());
		}
	}
	
}
