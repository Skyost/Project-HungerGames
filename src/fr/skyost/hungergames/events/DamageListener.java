package fr.skyost.hungergames.events;

import java.util.Random;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.HungerGames.Step;
import fr.skyost.hungergames.HungerGamesAPI;
import fr.skyost.hungergames.utils.Utils;

public class DamageListener implements Listener {
	
	@EventHandler
	private final void onEntityDamage(final EntityDamageEvent event) {
		final Entity entity = event.getEntity();
		if(entity.getType() == EntityType.PLAYER) {
			final Player player = (Player)entity;
			final World world = entity.getWorld();
			if(world.equals(HungerGames.lobby) || HungerGames.spectatorsManager.hasSpectator(player) || (world.equals(HungerGames.currentMap) && HungerGames.currentStep != Step.GAME)) {
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
						killerPlayer.sendMessage(HungerGames.messages.message15.replaceAll("/n/", String.valueOf(exp)).replaceAll("/player/", player.getName()));
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
						playerKey.sendMessage(HungerGames.messages.deathMessages.get(new Random().nextInt(HungerGames.messages.deathMessages.size())).replaceAll("/player/", player.getName()));
						world.playSound(playerKey.getLocation(), HungerGames.config.gameDeathSoundSound, HungerGames.config.gameDeathSoundVolume, HungerGames.config.gameDeathSoundPitch);
					}
					player.sendMessage(HungerGames.config.spectatorsEnable ? HungerGames.messages.message9 + (HungerGames.totalPlayers <= 2 ? "" : "\n" + HungerGames.messages.message12) : HungerGames.messages.message9);
					HungerGamesAPI.removePlayer(player);
				}
			}
		}
	}
	
	@EventHandler
	private final void onFoodLevelChange(final FoodLevelChangeEvent event) {
		final HumanEntity entity = event.getEntity();
		if(entity.getType() == EntityType.PLAYER) {
			final World world = entity.getWorld();
			if((world.equals(HungerGames.lobby) || HungerGames.spectatorsManager.hasSpectator((Player)entity) || (world.equals(HungerGames.currentMap) && HungerGames.currentStep != Step.GAME))) {
				event.setCancelled(true);
			}
		}
	}
	
}
