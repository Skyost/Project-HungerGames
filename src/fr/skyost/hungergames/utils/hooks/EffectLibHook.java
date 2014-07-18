package fr.skyost.hungergames.utils.hooks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import de.slikey.effectlib.EffectLib;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.effect.BleedEntityEffect;
import de.slikey.effectlib.effect.TextLocationEffect;
import de.slikey.effectlib.util.ParticleEffect;
import fr.skyost.hungergames.HungerGames;

public class EffectLibHook {
	
	private final EffectManager manager = new EffectManager(EffectLib.instance());
	
	/**
	 * Write a text in particles at the specified location.
	 * 
	 * @param text The text to write.
	 * @param location The location where the text will be write.
	 */
	
	public final void writeTextInParticles(final String text, final Location location) {
		final TextLocationEffect effect = new TextLocationEffect(manager, location);
		effect.text = text;
		effect.particle = ParticleEffect.FLAME;
		effect.start();
		Bukkit.getScheduler().scheduleSyncDelayedTask(HungerGames.instance, new Runnable() {

			@Override
			public void run() {
				effect.cancel(false);
			}
			
		}, 200L);
	}
	
	/**
	 * Used to bleed an entity.
	 * 
	 * @param entity The entity.
	 */
	
	public final void bleed(final Entity entity) {
		final BleedEntityEffect effect = new BleedEntityEffect(manager, entity);
		effect.duration = 1;
		effect.iterations = 1;
		effect.start();
	}
	
	/**
	 * Stop the instance of <b>EffectLibHook</b>. It cannot be used anymore.
	 */
	
	public final void stop() {
		manager.dispose();
	}

}