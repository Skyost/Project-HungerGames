package fr.skyost.hungergames.events.configurable;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.skyost.hungergames.HungerGames;

public class BloodListener implements Listener {
	
	@EventHandler
	private final void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
		final Entity entity = event.getEntity();
		if(entity.getWorld().equals(HungerGames.currentMap)) {
			if(event.getEntityType() == EntityType.PLAYER) {
				if(HungerGames.config.gameBloodPlayers) {
					bleed(entity);
				}
			}
			else if(HungerGames.config.gameBloodMobs) {
				bleed(entity);
			}
		}
	}
	
	public static final void bleed(final Entity entity) {
		if(HungerGames.effectLibHook != null) {
			HungerGames.effectLibHook.bleed(entity);
		}
		else {
			entity.getWorld().playEffect(entity.getLocation(), Effect.STEP_SOUND, Material.REDSTONE_WIRE);
		}
	}

}
