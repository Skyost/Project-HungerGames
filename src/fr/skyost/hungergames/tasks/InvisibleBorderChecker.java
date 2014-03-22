package fr.skyost.hungergames.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.skyost.hungergames.HungerGames;

public class InvisibleBorderChecker extends BukkitRunnable {
	
	private final int xMax;
	private final int xMin;
	private final int zMax;
	private final int zMin;

	public InvisibleBorderChecker(final int xMax, final int xMin, final int zMax, final int zMin) {
		this.xMax = xMax;
		this.xMin = xMin;
		this.zMax = zMax;
		this.zMin = zMin;
	}
	
	@Override
	public void run() {
		for(final Player player : HungerGames.players.keySet()) {
			if(correctLocation(player)) {
				player.sendMessage(HungerGames.messages.Messages_19);
			}
		}
	}
	
	public final boolean correctLocation(final Player player) {
		final Location location = player.getLocation();
		final int x = location.getBlockX();
		final int z = location.getBlockZ();
		if(x > xMax) {
			player.teleport(location.subtract(x - xMax, 0, 0));
			return true;
		}
		if(x < xMin) {
			player.teleport(location.add(xMin - x, 0, 0));
			return true;
		}
		if(z > zMax) {
			player.teleport(location.subtract(0, 0, z - zMax));
			return true;
		}
		if(z < zMin) {
			player.teleport(location.add(0, 0, zMin - z));
			return true;
		}
		return false;
	}
	
}
