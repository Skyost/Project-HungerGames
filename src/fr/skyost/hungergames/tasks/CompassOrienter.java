package fr.skyost.hungergames.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.hungergames.HungerGames;

public class CompassOrienter extends BukkitRunnable {

	@Override
	public void run() {
		for(final Player player : HungerGames.players.keySet()) {
			final Player nearest = getNearestPlayer(player);
			if(nearest != null) {
				player.setCompassTarget(nearest.getLocation());
			}
		}
	}

	private final Player getNearestPlayer(final Player player) {
		double distNear = 0.0D;
		Player nearest = null;
		final Location playerLocation = player.getLocation();
		for(final Player target : HungerGames.players.keySet()) {
			if(player.equals(target) || HungerGames.spectatorsManager.hasSpectator(target)) {
				continue;
			}
			final double dist = playerLocation.distance(target.getLocation());
			if(nearest == null || dist < distNear) {
				nearest = target;
				distNear = dist;
			}
		}
		return nearest;
	}

}
