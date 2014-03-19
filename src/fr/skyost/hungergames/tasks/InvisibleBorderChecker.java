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
			if(!validLocation(player.getLocation())) {
				player.sendMessage(HungerGames.messages.Messages_19);
				player.teleport(HungerGames.players.get(player).getGeneratedLocation());
			}
		}
	}
	
	public final boolean validLocation(final Location location) {
		final int x = location.getBlockX();
		final int z = location.getBlockZ();
		if((x > xMax) || (x < xMin) || (z > zMax) || (z < zMin)) {
			return false;
		}
		return true;
	}
	
}
