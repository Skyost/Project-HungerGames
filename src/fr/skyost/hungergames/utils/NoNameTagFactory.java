package fr.skyost.hungergames.utils;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NoNameTagFactory implements Listener {

	/*
	* Class made by BigTeddy98.
	*
	* NoNameTagFactory is a class based on the idea and techniques from
	* Kazzababe, but without NMS. This class allows you to hide nametags completely.
	*
	* Check out his version here:
	* https://forums.bukkit.org/threads/nonametags-completely-hide-a-players-nametag.288344/
	*
	* 1. No warranty is given or implied.
	* 2. All damage is your own responsibility.
	* 3. If you want to use this in your plugins, a credit would we appreciated.
	*/

	private Plugin plugin;

	public NoNameTagFactory(Plugin plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
	}

	public boolean hasNameTag(Player p) {
		if(p.getPassenger() != null && p.getPassenger().getType() == EntityType.SQUID && ((LivingEntity)p.getPassenger()).hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			return false;
		}
		return true;
	}

	public void setNameTag(Player p, boolean visible) {

		if(!visible) {
			// setNameTagInvisible
			if(hasNameTag(p)) {
				LivingEntity ent = p.getWorld().spawn(p.getLocation(), Squid.class);
				ent.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
				p.setPassenger(ent);
			}
		}
		else {
			// setNameTagVisible
			if(!hasNameTag(p)) {
				LivingEntity ent = (LivingEntity)p.getPassenger();
				p.eject();
				ent.remove();
			}
		}
	}

	@EventHandler
	private void onDamage(EntityDamageEvent event) {
		if(event.getEntity().getType() == EntityType.SQUID && ((LivingEntity)event.getEntity()).hasPotionEffect(PotionEffectType.INVISIBILITY)) {
			event.setCancelled(true);
		}
	}
	
}