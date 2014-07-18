package fr.skyost.hungergames.utils.borders.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.utils.borders.Border;
import fr.skyost.hungergames.utils.borders.BorderParams;

public class Invisible extends Border implements Runnable {
	
	private int xMax;
	private int xMin;
	private int zMax;
	private int zMin;
	
	@Override
	protected void createBorder(final BorderParams params) {
		final int x = params.getX();
		final int z = params.getZ();
		final int radius = params.getRadius();
		xMax = x + radius;
		xMin = x - radius;
		zMax = z + radius;
		zMin = z - radius;
		HungerGames.tasks[5] = Bukkit.getScheduler().scheduleSyncRepeatingTask(HungerGames.instance, this, 0, 60L);
	}

	@Override
	public Type getType() {
		return Type.INVISIBLE;
	}

	@Override
	public String getDescription() {
		return "Creates an invisible wall around the map.";
	}
	
	@Override
	public void run() {
		for(final Player player : HungerGames.players.keySet()) {
			if(wrongLocation(player)) {
				player.sendMessage(HungerGames.messages.message19);
			}
		}
	}
	
	private final boolean wrongLocation(final Player player) {
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
