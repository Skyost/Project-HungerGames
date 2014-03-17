package fr.skyost.hungergames.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.utils.Utils;

public class EntityListener  implements Listener {
	
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
	
}
