package fr.skyost.hungergames.utils.borders.types;

import org.bukkit.Bukkit;
import fr.skyost.hungergames.HungerGames;
import fr.skyost.hungergames.tasks.InvisibleBorderChecker;
import fr.skyost.hungergames.utils.borders.Border;
import fr.skyost.hungergames.utils.borders.BorderParams;

public class Invisible extends Border {
	
	@Override
	protected void createBorder(BorderParams bp) {
		final int x = bp.getX();
		final int z = bp.getZ();
		final int radius = bp.getRadius();
		HungerGames.tasks.set(5, Bukkit.getScheduler().scheduleSyncRepeatingTask(HungerGames.instance, new InvisibleBorderChecker(x + radius, x - radius, z + radius, z - radius), 0, 60L));
	}

	@Override
	public Type getType() {
		return Type.INVISIBLE;
	}

	@Override
	public String getDescription() {
		return "Creates an invisible wall around the map.";
	}
	
}
